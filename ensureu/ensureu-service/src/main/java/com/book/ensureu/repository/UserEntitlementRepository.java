package com.book.ensureu.repository;

import java.util.List;

import com.book.ensureu.constant.EntitlementType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.UserEntitlement;

@Repository
public interface UserEntitlementRepository extends MongoRepository<UserEntitlement, Long> {

	@Query("{ 'userId' : ?0, 'active' : ?1, 'createdDate' :{'$lte' : ?2}, 'validity':{'$gte' : ?3}, 'entitlementType': ?4 }")
	public List<UserEntitlement> findByUserIdAndActiveAndCreatedDateAndValidityAndEntitlementType(String userId, Boolean active,
																								  Long createdDate, Long validity, EntitlementType entitlementType);
	
	//@Query("{ 'userId' : ?0, 'active' : ?1,'createdDate' :{'$lte' : ?2}, 'validity':{'$gte' : ?3}, 'paperType' : ?4,'testType' : ?5 }")
	@Query("{'$and': [ {'userId' : ?0}, {'active' : ?1},{'createdDate' :{'$lte' : ?2}}, {'validity':{'$gte' : ?3}},{'paperType' : ?4},{'testType' : ?5}, {'entitlementType': ?6} ]}")
	public List<UserEntitlement> findByUserIdAndActiveAndCreatedDateAndValidityAndPaperTypeAndTestTypeAndEntitlementType(String userId, Boolean active,
																														 Long createdDate, Long validity, PaperType paperType,
																														 TestType testType, EntitlementType entitlementType);
	
	@Query("{ 'userId' : ?0, 'active' : ?1, 'validity' : {'$gte' : ?2 } }")
	public List<UserEntitlement> findByUserIdAndActiveAndGreaterThanValidity(String userId, Boolean active, Long validity);
	
	@Query("{ 'userId' : ?0, 'paperType' : ?1, 'paperCategory' :?2, 'active' : ?3, 'validity' : {'$gte' : ?4 },'entitlementType': ?5  }")
	public List<UserEntitlement> findByUserIdAndPaperTypeAndPaperCategoryActiveAndGreaterThanValidityAndEntitlementType(String userId,PaperType paperType,PaperCategory paperCategory,
			Boolean active, Long validity, EntitlementType entitlementType);

	public List<UserEntitlement> findByUserIdAndActiveAndEntitlementType(String userId, boolean active, EntitlementType entitlementType);
	
	@Query("{ 'validity' : {'$gte' : ?1, '$lte' : ?2}, 'active':true}")
	public List<UserEntitlement> findAllByActiveAndByValidityIn(boolean active, Long validityMin, long validityMax);

	@Query("{ 'userId' : ?0, 'active' : ?1, 'entitlementType' : ?2 }")
	public List<UserEntitlement> findByUserIdAndEntitlementType(String userId, boolean active, EntitlementType entitlementType);
	
	@Query("{'$and' : [ {'userId' : ?0}, {'active' : ?1}, {'paperType' : ?2} ,{'testType' : ?3}, {entitlementType : ?4} ] }")
	public List<UserEntitlement> findByUserIdAndPaperTypeAndTestTypeAndEntitlementType(String userId, boolean active, PaperType paperType, TestType testType,EntitlementType entitlementType);

	@Query("{ 'userId' : ?0, 'testSeriesId' : ?1, 'entitlementType' :?2, 'active' : ?3, 'validity' : {'$gte' : ?4 }}")
	public List<UserEntitlement> findByUserIdAndTestSeriesIdAndEntitlementTypeAndActiveAndValidityBefore(String userId,String testSeriesId,EntitlementType entitlementType,
																														Boolean active, Long validity);

	@Query("{ 'userId' : ?0, 'entitlementType' :?1, 'active' : ?2, 'validity' : {'$gte' : ?3 }}")
	public List<UserEntitlement> findByUserIdAndEntitlementTypeAndActiveAndValidityBefore(String userId,EntitlementType entitlementType,
																										 Boolean active, Long validity);

	// Methods that accept list of EntitlementTypes to query both SUBSCRIPTION and FREE_SUBSCRIPTION

	@Query("{ 'userId' : ?0, 'active' : ?1, 'createdDate' :{'$lte' : ?2}, 'validity':{'$gte' : ?3}, 'entitlementType': {'$in': ?4} }")
	public List<UserEntitlement> findByUserIdAndActiveAndCreatedDateAndValidityAndEntitlementTypeIn(String userId, Boolean active,
																									Long createdDate, Long validity, List<EntitlementType> entitlementTypes);

	@Query("{ 'userId' : ?0, 'paperType' : ?1, 'paperCategory' :?2, 'active' : ?3, 'validity' : {'$gte' : ?4 },'entitlementType': {'$in': ?5}  }")
	public List<UserEntitlement> findByUserIdAndPaperTypeAndPaperCategoryActiveAndGreaterThanValidityAndEntitlementTypeIn(String userId, PaperType paperType, PaperCategory paperCategory,
																														  Boolean active, Long validity, List<EntitlementType> entitlementTypes);

	@Query("{ 'userId' : ?0, 'active' : ?1, 'entitlementType' : {'$in': ?2} }")
	public List<UserEntitlement> findByUserIdAndActiveAndEntitlementTypeIn(String userId, boolean active, List<EntitlementType> entitlementTypes);

	@Query("{'$and' : [ {'userId' : ?0}, {'active' : ?1}, {'paperType' : ?2} ,{'testType' : ?3}, {'entitlementType' : {'$in': ?4}} ] }")
	public List<UserEntitlement> findByUserIdAndPaperTypeAndTestTypeAndEntitlementTypeIn(String userId, boolean active, PaperType paperType, TestType testType, List<EntitlementType> entitlementTypes);

	@Query("{'$and': [ {'userId' : ?0}, {'active' : ?1},{'createdDate' :{'$lte' : ?2}}, {'validity':{'$gte' : ?3}},{'paperType' : ?4},{'testType' : ?5}, {'entitlementType': {'$in': ?6}} ]}")
	public List<UserEntitlement> findByUserIdAndActiveAndCreatedDateAndValidityAndPaperTypeAndTestTypeAndEntitlementTypeIn(String userId, Boolean active,
																														   Long createdDate, Long validity, PaperType paperType,
																														   TestType testType, List<EntitlementType> entitlementTypes);

	/*
	 * public List<UserEntitlement> findByUserIdAndPaperTypeAndActive(Long userId,
	 * PaperType paperType, Boolean active);
	 * 
	 * public void deleteBySubscriptionIdAndUserId(Long subscriptionId, Long
	 * userId);
	 * 
	 * public List<UserEntitlement> findByUserIdAndPaperCategoryAndActive(Long
	 * userId, PaperCategory paperCategory, Boolean active);
	 * 
	 * public List<UserEntitlement> findByUserIdAndPaperSubCategoryAndActive(Long
	 * userId, PaperSubCategory paperSubCategory, Boolean active);
	 */

}
