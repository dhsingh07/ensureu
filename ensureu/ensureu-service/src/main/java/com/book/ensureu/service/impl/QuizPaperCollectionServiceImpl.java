package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.QuizPaperCollection;
import com.book.ensureu.repository.QuizPaperCollectionRepository;
import com.book.ensureu.service.QuizPaperCollectionService;
import com.mongodb.MongoException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuizPaperCollectionServiceImpl implements QuizPaperCollectionService {

	@Autowired
	@Lazy
	private QuizPaperCollectionRepository quizPaperCollectionRepository;
	
	@Override
	public void createQuizPaperInCollection(List<QuizPaperCollection> quizPaperCollection) throws MongoException {
		log.info("save list of quiz -->");
		quizPaperCollectionRepository.saveAll(quizPaperCollection);
	}

	@Override
	public void createQuizPaperInCollection(QuizPaperCollection quizPaperCollection) throws MongoException {
		
		try {
			if (quizPaperCollection != null && quizPaperCollection.getId() != null) {
				QuizPaperCollection quizPaperCollectionInDb = getQuizPaperCollectionById(quizPaperCollection.getId());
				if (quizPaperCollectionInDb != null && quizPaperCollectionInDb.getId() != null) {
					
					//to do for selecte fileds only.
					
					quizPaperCollection.setId(quizPaperCollectionInDb.getId());
				}
			}
			log.info("[quizPaperCollection type--->" + quizPaperCollection.getPaperType() + "quizpaper collection id: "
					+ quizPaperCollection.getId());
			quizPaperCollectionRepository.save(quizPaperCollection);

		} catch (Exception ex) {
			log.info("quizPaperCollection save--->" + quizPaperCollection.getId(), ex);
			throw ex;
		}
		
		
	}

	@Override
	public QuizPaperCollection getQuizPaperCollectionById(String id) throws MongoException, DataAccessException {
		Optional<QuizPaperCollection> quizPaper = quizPaperCollectionRepository.findById(id);
		if (quizPaper.isPresent()) {
			return quizPaper.get();
		}
		return null;
	}

	@Override
	public Page<QuizPaperCollection> getAllQuizPaperCollection(Pageable pageable)
			throws MongoException, DataAccessException {
		return quizPaperCollectionRepository.findAll(pageable);
	}

}
