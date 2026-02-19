package com.book.ensureu.admin.service;

import java.util.List;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.PaidPaperCollection;
import com.mongodb.MongoException;

public interface PaidPaperCollectionService {

	public void createTestPaperInCollection(List<PaidPaperCollection> testPaperCollection) throws MongoException;

	public void createPaidPaperInCollection(PaidPaperCollection testPaperCollection) throws MongoException;
	
	public void updatePaidPaperState(String id, PaperStateStatus paperStateStatus);

	public void updatePaidPaperStateWithValidity(String id, PaperStateStatus paperStateStatus,
			Long validityStartDate, Long validityEndDate);

	PaperCollectionDto getTestPaperCollectionById(String id) throws MongoException, DataAccessException;
	Page<PaperCollectionDto> getAllPaidPaperCollection(PaperType paperType,Pageable pageable) throws MongoException,DataAccessException;

	public List<PaperInfo> fetchPaperInfoList(PaperSubCategory paperSubCategory, Pageable pageable,Boolean taken);

	public List<PaperInfo> fetchFreshPaperInfoList(List<String> paperIds);

	public void setTakenPaidPaperCollectionFlag(List<String> paperIdList, boolean flag);

	/**
	 * Delete a paid paper by ID
	 * @param id Paper ID to delete
	 * @throws IllegalArgumentException if paper is ACTIVE or APPROVED
	 */
	void deletePaidPaper(String id) throws IllegalArgumentException;

}
