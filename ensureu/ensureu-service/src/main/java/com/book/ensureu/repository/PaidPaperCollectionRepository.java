package com.book.ensureu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PaidPaperCollection;

@Repository
public interface PaidPaperCollectionRepository extends MongoRepository<PaidPaperCollection, String> {

	public Optional<PaidPaperCollection> findById(String id);

	public List<PaidPaperCollection> findByIdIn(List<String> ids);

	public List<PaidPaperCollection> findByPaperType(String paperType);

	public long countByPaperTypeAndTestType(PaperType paperType, TestType testType);

	public long countByPaperType(PaperType paperType);

	public long countByPaperTypeAndPaperCategory(PaperType paperType, PaperCategory paperCategory);

	public long countByPaperTypeAndPaperCategoryAndTestType(PaperType paperType, PaperCategory paperCatogory,
			TestType testType);

	public long countByPaperTypeAndPaperCategoryAndPaperSubCategoryAndTestType(PaperType paperType,
			PaperCategory paperCatogory, PaperSubCategory paperSubCatogory, TestType testType);

	public long countByPaperCategory(PaperCategory paperCategory);
	public long countByPaperStateStatus(PaperStateStatus paperStateStatus);

	public List<Long> findTOP30ByPaperTypeAndTakenOrderByPriorty(PaperType paperType, boolean taken);

	@Query(value = "{'_id' :{ '$in':?0 }}",
			fields = "{ paperName : 1, _id : -1, id : -1, paperType : 1, paperCategory : 1, paperSubCategory : 1, testType: 1, paperSubCategoryName: 1, totalScore: 1,totalTime: 1,negativeMarks: 1,perQuestionScore: 1, totalQuestionCount: 1,paperStateStatus: 1,createDateTime:1, pattern:-1,paper:-1}")
	public List<PaidPaperCollection> findByIdListIns(List<String> ids) throws Exception;
	
	@Query(value = "{'$and':[{'paperStateStatus' :{ '$in':?0 }},{'paperType':?1}]}",
			fields = "{ paperName : 1, _id : 1, id : 1, paperType : 1, paperCategory : 1, paperSubCategory : 1, testType: 1, paperSubCategoryName: 1, totalScore: 1,totalTime: 1,negativeMarks: 1,perQuestionScore: 1, totalQuestionCount: 1,paperStateStatus: 1,createDateTime:1}")
	public Page<PaidPaperCollection> findByPaperStateStatusIn(List<PaperStateStatus> paperStateStatus,PaperType paperType,Pageable pageable) throws Exception;
	
	@Query(value = "{ 'paperSubCategory' : ?0 , 'taken' : ?1 , 'testType' : ?2 , 'validityRangeStartDateTime' : { '$lt' : ?3}, 'validityRangeEndDateTime' : { '$gt' : ?4} }")
	public List<PaidPaperCollection> findByPaperSubCategoryAndTaken(PaperSubCategory paperSubCategory, 
			boolean taken, TestType testType, Long validityRangeStartDateTime, Long validityRangeEndDateTime);

	@Query(value = "{ 'paperSubCategory' : ?0 , 'taken' : ?1 }",
			fields = "{ paperName: 1, _id:1 , paperSubCategory:1, createDateTime:1 , validityRangeEndDateTime:1 }"
	)
	public List<PaidPaperCollection> findByPaperSubCategoryAndTaken(PaperSubCategory paperSubCategory,
																	boolean taken,Pageable pageable);

	// Find papers by paperSubCategory and testType for subscription fallback
	@Query(value = "{ 'paperSubCategory' : ?0 , 'testType' : ?1 , 'paperStateStatus' : 'ACTIVE' }",
			fields = "{ paperName: 1, _id:1 , paperSubCategory:1, paperCategory:1, paperType:1, testType:1 }")
	public List<PaidPaperCollection> findByPaperSubCategoryAndTestType(PaperSubCategory paperSubCategory, TestType testType);

	// ==========================================
	// Subscription Management Methods
	// ==========================================

	/**
	 * Find available papers for subscription selection
	 * Papers must be: not taken, in approved/active state, matching sub-category
	 */
	@Query(value = "{ 'paperSubCategory': ?0, 'taken': ?1, 'paperStateStatus': { '$in': ?2 } }",
			fields = "{ paperName: 1, _id: 1, paperType: 1, paperCategory: 1, paperSubCategory: 1, testType: 1, " +
					"paperSubCategoryName: 1, totalScore: 1, totalTime: 1, negativeMarks: 1, perQuestionScore: 1, " +
					"totalQuestionCount: 1, paperStateStatus: 1, createDateTime: 1, taken: 1, priorty: 1 }")
	Page<PaidPaperCollection> findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
			PaperSubCategory paperSubCategory,
			boolean taken,
			List<PaperStateStatus> statuses,
			Pageable pageable);

	/**
	 * Count available papers for a sub-category
	 */
	long countByPaperSubCategoryAndTakenAndPaperStateStatusIn(
			PaperSubCategory paperSubCategory,
			boolean taken,
			List<PaperStateStatus> statuses);

	/**
	 * Search papers by name containing (case-insensitive)
	 */
	@Query(value = "{ 'paperSubCategory': ?0, 'taken': ?1, 'paperStateStatus': { '$in': ?2 }, " +
			"'paperName': { '$regex': ?3, '$options': 'i' } }",
			fields = "{ paperName: 1, _id: 1, paperType: 1, paperCategory: 1, paperSubCategory: 1, testType: 1, " +
					"totalQuestionCount: 1, totalTime: 1, paperStateStatus: 1, taken: 1 }")
	Page<PaidPaperCollection> findAvailablePapersWithSearch(
			PaperSubCategory paperSubCategory,
			boolean taken,
			List<PaperStateStatus> statuses,
			String searchTerm,
			Pageable pageable);

}

