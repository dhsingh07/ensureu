package com.book.ensureu.admin.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.FreePaperCollection;
import com.mongodb.MongoException;

public interface FreePaperCollectionService {

	public void createFreePaperInCollection(List<FreePaperCollection> freePaperCollection) throws MongoException;

	public void updateFreePaperState(String id, PaperStateStatus paperStateStatus);

	public void updateFreePaperStateWithValidity(String id, PaperStateStatus paperStateStatus,
			Long validityStartDate, Long validityEndDate);

	public void createFreePaperInCollection(FreePaperCollection freePaperCollection) throws MongoException;

	PaperCollectionDto getFreePaperCollectionById(String id) throws DataAccessException,MongoException;

	FreePaperCollection getFreePaperCollectionEntityById(String id) throws DataAccessException, MongoException;

	List<PaperCollectionDto> getFreePaperCollectionByIds(List<String> ids) throws DataAccessException,MongoException;
	
	Page<PaperCollectionDto> getAllFreePaperColl(PaperType paperType, Pageable pageable) throws DataAccessException,MongoException;
	
	Page<PaperCollectionDto> getAllFreePaperCollByTestType(Pageable pageable,String testType) throws DataAccessException,MongoException;

	/**
	 * Delete a free paper by ID
	 * @param id Paper ID to delete
	 * @throws IllegalArgumentException if paper is ACTIVE or APPROVED
	 */
	void deleteFreePaper(String id) throws IllegalArgumentException;
}
