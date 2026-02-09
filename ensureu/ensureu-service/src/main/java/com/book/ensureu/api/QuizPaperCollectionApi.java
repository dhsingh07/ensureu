package com.book.ensureu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.QuizPaperCollection;
import com.book.ensureu.service.QuizPaperCollectionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/quizPaperColl")
public class QuizPaperCollectionApi {


	@Autowired
	private QuizPaperCollectionService quizPaperCollectionService;
	
	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void saveQuizPaperCollection(@RequestBody QuizPaperCollection quizPaperCollection) {
		log.info("saveTestPaper ==={}",1);
		try {
			quizPaperCollectionService.createQuizPaperInCollection(quizPaperCollection);
		} catch (Exception ex) {
			log.error("saveQuizPaperCollection ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public QuizPaperCollection getQuizPaperById(@PathVariable(value = "id") final String id) {
		log.info("getQuizPaperById by ID {}", id);
		try {
			return quizPaperCollectionService.getQuizPaperCollectionById(id);
		} catch (Exception ex) {
			log.error("getQuizPaperById " + id, ex);
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Page<QuizPaperCollection> getAllQuizPaper(Pageable pageable) {
		log.info("getAllQuizPaper list {}",1);
		try {
			return quizPaperCollectionService.getAllQuizPaperCollection(pageable);
		} catch (Exception ex) {
			log.error("getAllQuizPaper ", ex);
		}
		return null;
	}
	
}
