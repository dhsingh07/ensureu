package com.book.ensureu.admin.api;

import org.slf4j.Logger;
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

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.FreePaperCollection;
import com.mongodb.MongoException;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/admin/freepapercoll")
public class FreePaperCollectionApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FreePaperCollectionApi.class);

	@Autowired
	FreePaperCollectionService freePaperCollectionService;

	//only for system user
	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void saveFreePaper(@RequestBody PaperCollectionDto paidPaperCollectionDto) {
		try {
			FreePaperCollection freePaperCollection=new FreePaperCollection();
			paidPaperCollectionDto.initializeDatesIfMissing();
			BeanUtils.copyProperties(paidPaperCollectionDto, freePaperCollection);
			freePaperCollectionService.createFreePaperInCollection(freePaperCollection);
		} catch (MongoException ex) {
			LOGGER.error("saveFreePaper ", ex);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public void updatePaper(@RequestParam(value="id",required=true) String id, @RequestParam(value="paperState",required=true) PaperStateStatus paperState) {
		try {
			freePaperCollectionService.updateFreePaperState(id, paperState);
		} catch (MongoException ex) {
			LOGGER.error("saveFreePaper ", ex);
		}
	}
	

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public PaperCollectionDto getFreePaperById(@PathVariable(value = "id") final String id) {
		try {
			return freePaperCollectionService.getFreePaperCollectionById(id);
		} catch (MongoException | DataAccessException e) {
			LOGGER.error("getFreePaperById " + id, e);
		}
		return null;

	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/list/{paperType}", method = RequestMethod.GET)
	public Page<PaperCollectionDto> getAllFreePaperFromColl(@PathVariable(value="paperType",required=true) String paperType, @RequestParam(value="page", required=true) int page,@RequestParam(value="size",required=true) int size) {
		LOGGER.info("getAllFreePaperFromColl list");
		try {
			Pageable pageable=PageRequest.of(page, size);
			return freePaperCollectionService.getAllFreePaperColl(PaperType.valueOf(paperType),pageable);
		} catch (Exception ex) {
			LOGGER.error("getAllFreePaperFromColl ", ex);
		}
		return null;
	}
	
	@CrossOrigin
	@RequestMapping(value = "/list/testType/{testType}", method = RequestMethod.GET)
	public Page<PaperCollectionDto> getAllFreePaperFromCollByTestType(@PathVariable(value="testType") String testType ,@RequestParam(value="page", required=true) int page,@RequestParam(value="size",required=true) int size) {
		LOGGER.info("getAllFreePaperFromCollByTestType list : "+testType);
		try {
			Pageable pageable=PageRequest.of(page, size);
			return freePaperCollectionService.getAllFreePaperCollByTestType(pageable,testType);
		} catch (Exception ex) {
			LOGGER.error("getAllFreePaperFromCollByTestType ", ex);
		}
		return null;
	}
	
}
