package com.book.ensureu.api;

import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.constant.*;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.security.UserPrincipalService;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.FreePaper;
import com.book.ensureu.service.FreePaperService;
import com.mongodb.MongoException;

import java.util.Optional;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/freepaper")
public class FreePaperApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FreePaperApi.class);

	
	@Autowired
	FreePaperService<FreePaper> freePaperService;
	@Autowired
	FreePaperCollectionService freePaperCollectionService;

	@Autowired
	UserPrincipalService userPrincipalService;
	
	@CrossOrigin
	@RequestMapping(value="/create",method=RequestMethod.POST)
	public void saveFreePaper(@RequestBody FreePaper practicePaper)
	{
		try {
			freePaperService.createFreePaper(practicePaper);
		}catch(MongoException me) {
			LOGGER.error("saveFreePaper  ",me);
		}
		
	}

	@CrossOrigin
	@RequestMapping(value="/create/user",method=RequestMethod.POST)
	public void mapUserFreePaper(@RequestBody FreePaperReq freePaperReq)
	{
		try {
			JwtUser currentUserDetails = userPrincipalService.getCurrentUserDetails();
			Optional<FreePaper> paperAssociatedUser = freePaperService.getFreePaperByPaperIdAndUserId(freePaperReq.getPaperId(), currentUserDetails.getUsername());
			if(!paperAssociatedUser.isEmpty()) {
				LOGGER.info("paper already assign paper id {} to user {}  ", freePaperReq.getPaperId(), currentUserDetails.getUsername());
				return;
			}
			FreePaperCollection freePaperCollection = freePaperCollectionService.getFreePaperCollectionEntityById(freePaperReq.getPaperId());
			FreePaper freePaper = new FreePaper();
			freePaper.setPaperId(freePaperReq.getPaperId());
			freePaper.setUserId(currentUserDetails.getUsername());
			freePaper.setPaperName(freePaperReq.getPaperName());
			freePaper.setPaperType(freePaperReq.getPaperType());
			freePaper.setTestType(freePaperReq.getTestType());
			freePaper.setPaperStatus(freePaperReq.getPaperStatus());
			freePaper.setPaperCategory(freePaperReq.getPaperCategory());
			freePaper.setPaperSubCategory(freePaperReq.getPaperSubCategory());
			freePaper.setPaper(freePaperCollection);

			freePaperService.createFreePaper(freePaper);
		}catch(MongoException me) {
			LOGGER.error("saveFreePaper  ",me);
		}

	}

	
	@CrossOrigin
	@RequestMapping(value="/getbyid/{id}",method=RequestMethod.GET)
	public FreePaper getFreePaperById(@PathVariable (value="id") final Long id) {
		FreePaper freePaper=null;
		try {
			LOGGER.info("getFreePaperById {}",id);
			freePaperService.getFreePaperById(id).orElseThrow(()-> new IllegalArgumentException("Data not found for this id "+ id));
			freePaper=freePaperService.getFreePaperById(id).get();
		}catch(DataAccessException | MongoException e) {
			LOGGER.error("getFreePaperById {}",e);
		}
		return freePaper;
	}

	@Getter
	class FreePaperReq {
		private String paperId;
		private String userId;
		private PaperStatus paperStatus;
		private PaperType paperType;
		private PaperCategory paperCategory;
		private PaperSubCategory paperSubCategory;
		private String paperSubCategoryName;
		private String paperName;
		private TestType testType;
	}
	
}
