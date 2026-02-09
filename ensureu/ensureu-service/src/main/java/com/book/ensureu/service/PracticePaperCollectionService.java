package com.book.ensureu.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.book.ensureu.model.PracticePaperCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * @author jatin.bansal
 *
 */
public interface PracticePaperCollectionService {

	public void createPracticePaperInCollection(List<PracticePaperCollection> practicePaperCollection)
			throws MongoException;

	public void createPracticePaperInCollection(PracticePaperCollection practicePaperCollection) throws MongoException;

	PracticePaperCollection getPracticeCollectionById(String id) throws MongoException, DataAccessException;

	Page<PracticePaperCollection> getAllPracticeCollection(Pageable pageable)
			throws MongoException, DataAccessException;
	
	List<DBObject> getTitleWiseCountAndQuestionsByPaperCategory(String paperCategory,String sectionTitle,String subSectionTitle);
	
	List<DBObject> getTitleWiseCountByPaperCategory(String paperCategory);

}
