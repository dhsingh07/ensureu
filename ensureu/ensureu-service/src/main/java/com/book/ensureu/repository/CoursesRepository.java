package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.CoursesModel;

/**
 * @author dharmendra.singh
 *
 */
@Repository
public interface CoursesRepository extends MongoRepository<CoursesModel, Long>{
	
	public CoursesModel findByName(String name);

}
