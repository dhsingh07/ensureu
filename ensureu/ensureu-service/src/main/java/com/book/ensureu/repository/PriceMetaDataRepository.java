package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.common.model.PriceMetaData;

public interface PriceMetaDataRepository extends MongoRepository<PriceMetaData, Long>{
	
	public List<PriceMetaData> findBySubscriptionTypeIn(List<SubscriptionType> subscriptionTypes);

	
}
