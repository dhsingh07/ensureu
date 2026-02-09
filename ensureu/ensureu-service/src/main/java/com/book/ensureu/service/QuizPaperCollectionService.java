package com.book.ensureu.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.model.QuizPaperCollection;
import com.mongodb.MongoException;

public interface QuizPaperCollectionService {

	public void createQuizPaperInCollection(List<QuizPaperCollection> quizPaperCollection) throws MongoException;

	public void createQuizPaperInCollection(QuizPaperCollection quizPaperCollection) throws MongoException;

	QuizPaperCollection getQuizPaperCollectionById(String id) throws MongoException, DataAccessException;

	Page<QuizPaperCollection> getAllQuizPaperCollection(Pageable pageable) throws MongoException, DataAccessException;
	
}
