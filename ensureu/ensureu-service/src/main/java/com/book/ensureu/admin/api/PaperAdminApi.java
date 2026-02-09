package com.book.ensureu.admin.api;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.ApplicationConstant;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.util.HashUtil;
import com.book.ensureu.util.PaperImageUploadHelper;
import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;
import com.mongodb.MongoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/paper")
public class PaperAdminApi {
	
	@Autowired
	private FreePaperCollectionService freePaperCollectionService;
	
	@Autowired
	private PaidPaperCollectionService paidPaperCollectionService;
	
	@Autowired
	private UserPrincipalService userPrincipalService;
	
	@Autowired
	private GoogleCloudStorageUtil googleCloudStorageUtil;

	//only for system user
	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void savePaper(@RequestBody PaperCollectionDto paperCollectionDto) {
		try {
			if (paperCollectionDto.getTestType().equals(TestType.FREE)) {
				FreePaperCollection freePaperCollection = new FreePaperCollection();
				BeanUtils.copyProperties(paperCollectionDto, freePaperCollection);
				log.info("Free Paper Save/update.....",freePaperCollection.getId());
				freePaperCollectionService.createFreePaperInCollection(freePaperCollection);
			} else if (paperCollectionDto.getTestType().equals(TestType.PAID)) {
				PaidPaperCollection paidPaperCollection = new PaidPaperCollection();
				BeanUtils.copyProperties(paperCollectionDto, paidPaperCollection);
				log.info("Paid Paper Save/update.....",paidPaperCollection.getId());
				paidPaperCollectionService.createPaidPaperInCollection(paidPaperCollection);
			}

		} catch (MongoException ex) {
			log.error("savePaper ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/save/{testType}", method = RequestMethod.PUT)
	public void updatePaper(@PathVariable(value="testType",required = true) String testType,@RequestParam(value="id",required=true) String id, @RequestParam(value="paperState",required=true) PaperStateStatus paperState) {
		try {
			if(TestType.FREE.equals(TestType.valueOf(testType))) {
				freePaperCollectionService.updateFreePaperState(id, paperState);
			}else if(TestType.PAID.equals(TestType.valueOf(testType))) {
				paidPaperCollectionService.updatePaidPaperState(id, paperState);
			}
			
		} catch (MongoException ex) {
			log.error("updatePaper ", ex);
		}
	}
	

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{testType}/{id}", method = RequestMethod.GET)
	public PaperCollectionDto getFreePaperById(@PathVariable(value="testType",required = true) String testType,@PathVariable(value = "id") final String id) {
		try {
			if(TestType.FREE.equals(TestType.valueOf(testType))) {
				return freePaperCollectionService.getFreePaperCollectionById(id);
			}else if(TestType.PAID.equals(TestType.valueOf(testType))) {
				return paidPaperCollectionService.getTestPaperCollectionById(id);			
				}
			
		} catch (MongoException | DataAccessException e) {
			log.error("getPaperById " + id, e);
		}
		return null;

	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/list/{paperType}/{testType}", method = RequestMethod.GET)
	public Page<PaperCollectionDto> getAllPaperFromColl(@PathVariable(value="paperType",required=true) String paperType,@PathVariable(value="testType",required = true) String testType, @RequestParam(value="page", required=true) int page,@RequestParam(value="size",required=true) int size) {
		log.info("getAllPaperFromColl list");
		try {
			Pageable pageable=PageRequest.of(page, size);
			if(TestType.FREE.equals(TestType.valueOf(testType))) {
				return freePaperCollectionService.getAllFreePaperColl(PaperType.valueOf(paperType),pageable);
			}else if(TestType.PAID.equals(TestType.valueOf(testType))) {
				return paidPaperCollectionService.getAllPaidPaperCollection(PaperType.valueOf(paperType), pageable);
			}
		} catch (Exception ex) {
			log.error("getAllPaperFromColl ", ex);
		}
		return null;
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/upload/image/{paperType}/{testType}", method = RequestMethod.POST)
	public Response<Object> savePaperImage(@PathVariable(value="paperType",required=true) String paperType, String testType, @RequestParam(value="file", required=true) MultipartFile file) {
		log.info("savePaperImage list");
		try {
			String userName=userPrincipalService.getCurrentUserDetails().getUsername();
			if(userName==null || userName.equals(""))
				throw new IllegalAccessError("User Not authorise");
			String fileName=HashUtil.hashByMD5(userName,file.getOriginalFilename(),String.valueOf(System.nanoTime()),paperType,testType);
			String filePath=null;
			if(PaperType.SSC.equals(PaperType.valueOf(paperType))) {
				 filePath=ApplicationConstant.SSC_PAPER_BUCKET_FILE_PATH+"/"+fileName;
			}else {
				return Response.builder().message("Failed due to paperType").status(400).build(); 
			}
			
			String imageUrl=PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME, file, filePath);
			System.out.println(imageUrl);
			
			return Response.builder().body(fileName).message("Sucess image URL #"+imageUrl+"").status(200).build();
			
		} catch (Exception ex) {
			log.error("getAllPaperFromColl ", ex);
		}
		return Response.builder().message("Failed").status(400).build();
	}
}
