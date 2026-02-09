package com.book.ensureu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;

import com.book.ensureu.model.FreePaper;
import com.mongodb.MongoException;

public interface FreePaperService<T> {
	public void createFreePaper(List<T> freePaper) throws MongoException;
	
	public void createFreePaper(T freePaper) throws MongoException;

	public Optional<T> getFreePaperById(Long id) throws DataAccessException,MongoException;
	public Optional<T> getFreePaperByPaperIdAndUserId(String userId,  String paperId) throws DataAccessException,MongoException;


}
