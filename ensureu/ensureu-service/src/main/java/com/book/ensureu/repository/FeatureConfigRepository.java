package com.book.ensureu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.admin.model.FeatureConfigModel;

@Repository
public interface FeatureConfigRepository extends MongoRepository<FeatureConfigModel, String> {
}
