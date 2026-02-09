package com.book.ensureu.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.PastPaperCollection;
import com.mongodb.MongoException;

public interface PastPaperCollectionService {

	public void createPastPaperInCollection(List<PastPaperCollection> pastPaperCollection) throws MongoException;

	public void createPastPaperInCollection(PastPaperCollection pastPaperCollection) throws MongoException;

	PastPaperCollection getPastPaperCollectionById(String id) throws MongoException, DataAccessException;

	Page<PastPaperCollection> getAllPastPaperCollection(Pageable pageable) throws MongoException, DataAccessException;

}
