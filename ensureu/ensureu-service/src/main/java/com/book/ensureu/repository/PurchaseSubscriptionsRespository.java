package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.PurchaseStatus;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PurchaseSubscriptions;

@Repository
public interface PurchaseSubscriptionsRespository extends MongoRepository<PurchaseSubscriptions, Long> {

	public List<PurchaseSubscriptions> findByUserIdAndPaperTypeAndTestType(String userId,PaperType paperType,TestType testType);

	/**
	 * Find active subscriptions for a user by category where validity is not expired
	 */
	@Query("{'userId': ?0, 'paperCategory': ?1, 'parchaseStatus': ?2, 'validity': {$gte: ?3}}")
	public List<PurchaseSubscriptions> findActiveSubscriptionsByUserAndCategory(
			String userId,
			PaperCategory paperCategory,
			PurchaseStatus purchaseStatus,
			Long currentTimeMs);

	/**
	 * Find active subscriptions for a user by paper type where validity is not expired
	 */
	@Query("{'userId': ?0, 'paperType': ?1, 'parchaseStatus': ?2, 'validity': {$gte: ?3}}")
	public List<PurchaseSubscriptions> findActiveSubscriptionsByUserAndPaperType(
			String userId,
			PaperType paperType,
			PurchaseStatus purchaseStatus,
			Long currentTimeMs);
}
