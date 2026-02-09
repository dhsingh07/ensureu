package com.book.ensureu.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.BlogsModel;

@Repository
public interface BlogsRepository extends MongoRepository<BlogsModel, String> {

	@Query("{'category.id': ?0}")
	Page<BlogsModel> findByCategory(ObjectId category, Pageable pageable);

	@Query("{'userId': ?0}")
	Page<BlogsModel> findByUserId(final String userId, Pageable pageable);

	List<BlogsModel> findBy(TextCriteria criteria);
	
	@Query("{'userId': ?0},")
	List<BlogsModel> updatePartial(ObjectId id,String permalink);

}
