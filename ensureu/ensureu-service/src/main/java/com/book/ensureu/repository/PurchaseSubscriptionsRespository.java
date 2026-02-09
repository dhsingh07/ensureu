package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PurchaseSubscriptions;

@Repository
public interface PurchaseSubscriptionsRespository extends MongoRepository<PurchaseSubscriptions, Long> {
	
public List<PurchaseSubscriptions> findByUserIdAndPaperTypeAndTestType(String userId,PaperType paperType,TestType testType);
}
