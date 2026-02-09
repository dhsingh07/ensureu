package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Subscription;
import com.book.ensureu.model.Subscription.SubscriptionState;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, Long> {

	public long countByState(SubscriptionState state);

	public List<Subscription> findTopByTestTypeOrderByAmendmentNoDesc(TestType testType);

	public List<Subscription> findTopByTestTypeAndPaperTypeOrderByAmendmentNoDesc(TestType testType,
			PaperType paperType);

	/*
	 * @Query( value =
	 * "find({'paperSubCategory':{$in:['SSC_CGL_TIER1','SSC_CGL_TIER2']} }).limit(1)"
	 * )
	 */
	@Query("{ 'paperSubCategory' : { '$in' : ?0 }, 'testType' : ?1 }")
	public List<Subscription> findByPaperSubCategoryInAndTestTypeOrderByAmendmentNoDesc(
			List<PaperSubCategory> paperSubCategories, TestType testType);
	// public List<Subscription> findTopBySubscriptionIdAnd
	
	public List<Subscription> findByIdIn(List<Long> listOfIds);
	
	public List<Long> findByIdInAndPaperType(List<Long> listOfIds, PaperType paperType);
	
	public List<Long> findByIdInAndPaperCategory(List<Long> listOfIds, PaperCategory paperCategory);
	
	public List<Long> findByIdInAndPaperSubCategory(List<Long> listOfIds, PaperSubCategory paperSubCategory);

	@Query("{ 'paperSubCategory' : { '$in' : ?0 }, 'testType' : ?1 , 'validity' : { '$gte' : ?2 }}")
	public List<Subscription> findByPaperSubCategoryInAndTestTypeOrder(List<PaperSubCategory> listOfSubCatogory,
			TestType testType, Long validity);
	
	@Query("{ 'paperSubCategory' : ?0 , 'testType' : ?1 , 'createdDate' : { '$lt' : ?2 } , 'validity' : { '$gt' : ?3 }}")
	public List<Subscription> findByPaperSubCategoryAndTestTypeOrder(PaperSubCategory paperSubCategory,
																	 TestType testType, Long createdDate, Long validity);
	
	@Query("{'$and': [{ 'paperType' : ?0 }, {'testType' : ?1} , {'createdDate' : { '$lt' : ?2 }} , {'validity' : { '$gt' : ?3 }}]}")
	public List<Subscription> findByPaperTypeAndTestType(PaperType paperType,
			TestType testType, Long createdDate, Long validity);
	
	@Query("{ '$and': [{'paperType' : ?0} ,{ 'paperCategory' :?1},{'testType' : ?2 }, {'createdDate' : { '$lt' : ?3 }} ,{ 'validity' : { '$gt' : ?4 }}]}")
	public List<Subscription> findByPaperTypeAndPaperCategoryAndTestType(PaperType paperType,
			PaperCategory paperCategory,TestType testType, Long createdDate, Long validity);

	// ==========================================
	// Subscription Management Methods (NEW)
	// ==========================================

	/**
	 * Find subscription by string ID (for API compatibility)
	 */
	public java.util.Optional<Subscription> findById(Long id);

	/**
	 * Find all subscriptions with state
	 */
	public List<Subscription> findByState(SubscriptionState state);

	/**
	 * Find subscriptions expiring within a date range
	 */
	@Query("{ 'state': 'ACTIVE', 'validity': { '$gte': ?0, '$lte': ?1 } }")
	public List<Subscription> findExpiringBetween(Long fromDate, Long toDate);

	/**
	 * Count active subscriptions
	 */
	public long countByStateAndTestType(SubscriptionState state, TestType testType);

	/**
	 * Find by paper type and state
	 */
	public List<Subscription> findByPaperTypeAndState(PaperType paperType, SubscriptionState state);

	/**
	 * Find subscriptions containing a specific paper ID
	 */
	@Query("{ 'paperIds': ?0, 'state': 'ACTIVE' }")
	public List<Subscription> findActiveByPaperId(String paperId);

	/**
	 * Find subscriptions by paper sub-category with pagination (for admin listing)
	 */
	@Query("{ 'paperSubCategory': ?0 }")
	public org.springframework.data.domain.Page<Subscription> findByPaperSubCategory(
			PaperSubCategory paperSubCategory,
			org.springframework.data.domain.Pageable pageable);

	/**
	 * Find all subscriptions with pagination (for admin listing)
	 */
	public org.springframework.data.domain.Page<Subscription> findAll(org.springframework.data.domain.Pageable pageable);

	/**
	 * Search subscriptions by name or description
	 */
	@Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
	public org.springframework.data.domain.Page<Subscription> searchByNameOrDescription(
			String searchTerm,
			org.springframework.data.domain.Pageable pageable);

}

