package com.book.ensureu.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.BlogCommentsModel;

@Repository
public interface BlogCommentsRepository extends MongoRepository<BlogCommentsModel, String> {

	Optional<BlogCommentsModel> findByBlogId(String blogId);

}
