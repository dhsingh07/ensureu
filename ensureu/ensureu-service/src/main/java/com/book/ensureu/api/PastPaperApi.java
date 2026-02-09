package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperAesDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.impl.PaperFactory;
import com.book.ensureu.util.AesEncDecUtil;
import com.book.ensureu.util.HashUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/pastpaper")
public class PastPaperApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(PastPaperApi.class.getName());
	@Autowired
	PaperFactory paperFactory;

	PaperService paperService;

	@Autowired
	UserPrincipalService userPrincipal;
	
	@Value("${aes.encryption.salt}")
	private String salt;
	
	@Value("${aes.encryption.iv}")
	private String iv;
	
	@Value("${aes.encryption.passphrase}")
	private String passphrase;
	
	private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void savePaper(@RequestBody PaperDto paperDto) {

		if (paperDto != null) {
			try {
				LOGGER.info("savePastPaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType());
				paperService = paperFactory.getPaperService(paperDto.getTestType().toString());
				paperService.savePaper(paperDto);

			} catch (Exception ex) {
				LOGGER.error("savePastPaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType(), ex);
				throw ex;
			}
		}

	}
	
	@CrossOrigin
	@RequestMapping(value = "/v1/save", method = RequestMethod.POST)
	public void savePaperV1(@RequestBody PaperAesDto<String> paperAesDto) {
		
		if(paperAesDto!=null && paperAesDto.getBody()!=null) {
			PaperDto paperDto=null;
			try {
			AesEncDecUtil aesEncDecUtil=new AesEncDecUtil();
			String passPhressEnr=HashUtil.setPassphraseEnrichment(passphrase);
			LOGGER.info("passPhressEnr  " + passPhressEnr);
			String paperDtoStr=aesEncDecUtil.decrypt(salt, iv, passPhressEnr, paperAesDto.getBody());
			paperDto=objectMapper.readValue(paperDtoStr, PaperDto.class);
		
			if (paperDto != null) {
				try {
					LOGGER.info("savePastPaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType());
					paperService = paperFactory.getPaperService(paperDto.getTestType().toString());
					paperService.savePaper(paperDto);

				} catch (Exception ex) {
					LOGGER.error("savePastPaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType(), ex);
					throw ex;
				}
			}
			
			} catch (Exception ex) {
				
				LOGGER.error("savePaper  " + paperDto.getPaperId() + " testType " + paperDto.getTestType(), ex);
				ex.printStackTrace();
			}
		}else {
			throw new IllegalArgumentException("Can't be null");
		}

	}
	

	@CrossOrigin
	@RequestMapping(value = "/{testType}/{paperId}", method = RequestMethod.GET)
	public List<PaperDto> getPaperByPaperId(@PathVariable(value = "paperId") final String paperId,
			@PathVariable(value = "testType") String testType) {

		try {
			LOGGER.info("getPaperByPaperId  " + paperId + " testType " + testType);
			paperService = paperFactory.getPaperService(testType);
			return paperService.getPaperByPaperId(paperId, TestType.valueOf(testType.toUpperCase()));

		} catch (Exception ex) {
			LOGGER.error("getPaperByPaperId  " + paperId + " testType " + testType, ex);
			throw ex;
		}
}
	
	
	/**
	 * service to mapped with user and saved collection to userPastPaper collection when
	 * status is start other wise just return from userPaper
	 * 
	 * @param testType
	 * @param paperStatus
	 * @param paperId
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/user/mapping/{testType}/{paperStatus}/{paperId}", method = RequestMethod.GET)
	public PaperDto getPaperAndUserByTestStatus(@PathVariable(value = "testType") String testType,
			@PathVariable(value = "paperStatus") String paperStatus, @PathVariable(value = "paperId") String paperId) {

		JwtUser jwtUser = null;
		try {
			jwtUser = userPrincipal.getCurrentUserDetails();
			LOGGER.info("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername());
			paperService = paperFactory.getPaperService(TestType.PASTPAPER.toString());
			return paperService.paperMappedUserByPaperStatus(jwtUser.getUsername(),
					TestType.PASTPAPER, PaperStatus.valueOf(paperStatus.toUpperCase()), paperId);

		} catch (Exception ex) {
			LOGGER.error("getPaperAndUserByTestStatus testType " + testType + " UserId " + jwtUser.getUsername()
					+ "paperId " + paperId, ex);
			throw ex;
		}

	}

	
	
	/**
	 * service to mapped with user and saved collection to userPaper collection when
	 * status is start other wise just return from userPaper
	 * 
	 * @param testType
	 * @param paperStatus
	 * @param paperId
	 * encrypted paper
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/v1/user/mapping/{testType}/{paperStatus}/{paperId}", method = RequestMethod.GET)
	public Response<String> getPaperAndUserByTestStatusEnc(@PathVariable(value = "testType") String testType,
			@PathVariable(value = "paperStatus") String paperStatus, @PathVariable(value = "paperId") String paperId) {

		JwtUser jwtUser = null;
		try {
			jwtUser = userPrincipal.getCurrentUserDetails();
			LOGGER.info("getPaperByUser testType " + testType + " UserId " + jwtUser.getUsername());
			paperService = paperFactory.getPaperService(TestType.PASTPAPER.toString());
			PaperDto paperDto= paperService.paperMappedUserByPaperStatus(jwtUser.getUsername(),
					TestType.PASTPAPER, PaperStatus.valueOf(paperStatus.toUpperCase()), paperId);
			
			AesEncDecUtil aesEncDecUtil=new AesEncDecUtil();
			String passPhressEnr=HashUtil.setPassphraseEnrichment(passphrase);
			LOGGER.info("passPhressEnr  " + passPhressEnr);
			String paperSdoStr=objectMapper.writeValueAsString(paperDto);
			String paperDtoEncStr=aesEncDecUtil.encrypt(salt, iv, passPhressEnr,paperSdoStr);
			
			return new Response<String>().setBody(paperDtoEncStr).setStatus(200).setMessage("Success");

		} catch (Exception ex) {
			LOGGER.error("getPaperAndUserByTestStatus testType " + testType + " UserId " + jwtUser.getUsername()
					+ "paperId " + paperId, ex);
		}
		return null;

	}
	
	
	
	
	/**
	 * it is used for getting pastpaper
	 * 
	 * @param testType
	 * @param paperIds
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "user/list/paperType/{paperType}", method = RequestMethod.GET)
	public List<PaperDto> getUserPapereTestStatus(@PathVariable(value = "paperType") String paperType,
			@RequestParam(value = "paperCategory", required = true) String paperCategory) {

		 JwtUser jwtUser = null;
		 String userId=null;
		try {
			if(userPrincipal!=null) {
			jwtUser = userPrincipal.getCurrentUserDetails();
			if(jwtUser!=null)
			userId=jwtUser.getUsername();
			
			}
			LOGGER.info("getUserPapereTestStatus testType " + TestType.PASTPAPER + " UserId " + "No User");
			PaperCategory paperCate = paperCategory != null ? PaperCategory.valueOf(paperCategory) : null;
				paperService = paperFactory.getPaperService(TestType.PASTPAPER.toString());
				return paperService.getPaperStatusDetailsByPaperCateoryORTestType(userId,
						TestType.PASTPAPER, PaperType.valueOf(paperType.toUpperCase()),
						paperCate);

		} catch (Exception ex) {
			LOGGER.error("getUserPapereTestStatus  testType " + TestType.PASTPAPER + " UserId " + "No User", ex);
		}

		return null;

	}
	
	/**
	 * @param paperType
	 * @param paperStatus
	 * @param paperCategory
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "user/paperType/{paperType}", method = RequestMethod.GET)
	public List<PaperDto> getUserPaperByStatus(@PathVariable(value = "paperType") String paperType,
			@RequestParam(value = "paperStatus", required = true) String paperStatus,
			@RequestParam(value = "paperCategory", required = false) String paperCategory) {

		JwtUser jwtUser = null;
		try {
			jwtUser = userPrincipal.getCurrentUserDetails();
			LOGGER.info("getUserPaperByStatus testType " + TestType.PASTPAPER + " UserId " + jwtUser.getUsername());
			PaperCategory paperCate = paperCategory != null ? PaperCategory.valueOf(paperCategory) : null;
				paperService = paperFactory.getPaperService(TestType.PASTPAPER.toString());
				return paperService.getPaperStatusDetailsByStatusAndPaperType(jwtUser.getUsername(),
						TestType.PASTPAPER, PaperType.valueOf(paperType.toUpperCase()),
						PaperStatus.valueOf(paperStatus.toUpperCase()), paperCate);


		} catch (Exception ex) {
			LOGGER.error("getUserPapereByStatus  testType " + TestType.PASTPAPER + " UserId " + jwtUser.getUsername(), ex);
		}
		return null;

	}
	

}