package com.book.ensureu.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.PaidPaper;
import com.mongodb.MongoException;

public interface TestPaperService {

	public void createTestPaper(List<PaidPaper> testPaper) throws MongoException;

	public void createTestPaper(PaidPaper testPaper) throws MongoException;
		
	public void createTestPaper(PaperDto testPaperDto) throws MongoException;

	public PaperDto getTestPaperById(Long id) throws MongoException;
	
	public List<PaperDto> getTestPaperByUserId(String userId) throws MongoException,DataAccessException;
	public List<PaperDto> getTestPaperByTestPaperId(String testPaperId) throws MongoException,DataAccessException;
	
	public PaperDto getTestPaperByUserIdAndTestPaperId(String userId, String testPaperId) throws MongoException,DataAccessException;
	

}
