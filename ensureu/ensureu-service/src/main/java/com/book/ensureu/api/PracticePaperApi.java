package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.PracticePaperCollection;
import com.book.ensureu.service.PracticePaperCollectionService;
import com.book.ensureu.util.PaperConversionUtil;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * @author jatin.bansal
 *
 */
@RestController
@RequestMapping("/practicepaper")
public class PracticePaperApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PracticePaperApi.class);

	@Autowired
	PracticePaperCollectionService practicePaperService;

	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void savePraticePaper(@RequestBody PracticePaperCollection practicePaper) {
		try {
			practicePaperService.createPracticePaperInCollection(practicePaper);
		} catch (MongoException me) {
			LOGGER.error("savePraticePaper  ", me);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPracticePaperById(@PathVariable(value = "id") final String id) {
		ResponseEntity<?> res = null;
		try {
			LOGGER.info("getPracticePaperById {}", id);
			PracticePaperCollection practicePaper = practicePaperService.getPracticeCollectionById(id);
			res = practicePaper != null ? ResponseEntity.ok(PaperConversionUtil.practicePaperToDto(practicePaper)) : ResponseEntity.notFound().build();
		} catch (DataAccessException | MongoException e) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("getPracticePaperById {}", e);
		}
		return res;

	}
	
	@CrossOrigin
	@RequestMapping(value="/count/questions/{categoryName}",method=RequestMethod.GET)
	public ResponseEntity<?> getTitleWiseCountAndQuestions(@PathVariable(value="categoryName") String categoryName,
			@RequestParam(value="sectionTitle",required = false) String sectionTitle,@RequestParam(value="subSectionTitle",required = false) String subSectionTitle){
		ResponseEntity<?> res = null;
		try{
			LOGGER.info("getTitleWiseCountAndQuestions {}", categoryName);
			List<DBObject> result = practicePaperService.getTitleWiseCountAndQuestionsByPaperCategory(categoryName,sectionTitle,subSectionTitle);
			res = result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
		}catch(Exception e){
			res = ResponseEntity.badRequest().build();
			LOGGER.error("getTitleWiseCountAndQuestions {}", e);
		}
		return res;
	}
	
	@CrossOrigin
	@RequestMapping(value="/count/{categoryName}",method=RequestMethod.GET)
	public ResponseEntity<?> getTitleWiseCount(@PathVariable(value="categoryName") String categoryName){
		ResponseEntity<?> res = null;
		try{
			LOGGER.info("getTitleWiseCount {}", categoryName);
			List<DBObject> result = practicePaperService.getTitleWiseCountByPaperCategory(categoryName);
			res = result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
		}catch(Exception e){
			res = ResponseEntity.badRequest().build();
			LOGGER.error("getTitleWiseCount {}", e);
		}
		return res;
	}

}
