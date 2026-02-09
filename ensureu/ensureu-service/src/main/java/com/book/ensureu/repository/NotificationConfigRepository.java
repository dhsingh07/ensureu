package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.model.NotificationConfigModel;

@Repository
public interface NotificationConfigRepository extends MongoRepository<NotificationConfigModel, String> {

	NotificationConfigModel findBySubCategory(String subCategory);
	
}
