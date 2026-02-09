package com.book.ensureu.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.PastPaperCollection;
import com.book.ensureu.service.PastPaperCollectionService;

@RestController
@RequestMapping("/pastpaperCollection")
public class PastPaperColletionApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(PastPaperColletionApi.class.getName());
	@Autowired
	private PastPaperCollectionService pastPaperCollectionService;
	
	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void savePastPaperCollection(@RequestBody PastPaperCollection pastPaperCollection) {
		LOGGER.info("saveTestPaper ==={}",1);
		try {
			pastPaperCollectionService.createPastPaperInCollection(pastPaperCollection);
		} catch (Exception ex) {
			LOGGER.error("savePastPaperCollection ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public PastPaperCollection getPastPaperById(@PathVariable(value = "id") final String id) {
		LOGGER.info("getPastPaperById by ID {}", id);
		try {
			return pastPaperCollectionService.getPastPaperCollectionById(id);
		} catch (Exception ex) {
			LOGGER.error("getPastPaperById " + id, ex);
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Page<PastPaperCollection> getAllPastPaper(Pageable pageable) {
		LOGGER.info("getAllPastPaper list {}",1);
		try {
			return pastPaperCollectionService.getAllPastPaperCollection(pageable);
		} catch (Exception ex) {
			LOGGER.error("getAllPastPaper ", ex);
		}
		return null;
	}
	
	
	
}
