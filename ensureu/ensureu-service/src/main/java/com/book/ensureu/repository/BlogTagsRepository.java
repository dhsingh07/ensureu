package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.BlogTagsModel;

@Repository
public interface BlogTagsRepository extends MongoRepository<BlogTagsModel, String> {

}
