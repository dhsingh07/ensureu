package com.book.ensureu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.QuizPaperCollection;

public interface QuizPaperCollectionRepository extends MongoRepository<QuizPaperCollection, String>{

	public Optional<QuizPaperCollection> findById(String id);

	public List<QuizPaperCollection> findByPaperType(String paperType);

	public long countByPaperTypeAndPaperCategoryAndTestType(PaperType paperType, PaperCategory paperCatogory,
			TestType testType);

	public long countByPaperTypeAndPaperCategoryAndPaperSubCategoryAndTestType(PaperType paperType,
			PaperCategory paperCatogory, PaperSubCategory paperSubCatogory, TestType testType);


	@Query(value = "{'_id' :{ '$in':?0 }}",
			fields = "{ paperName : 1, _id : -1, id : -1, paperType : 1, paperCategory : 1, paperSubCategory : 1, testType: 1, paperSubCategoryName: 1, totalScore: 1,totalTime: 1,negativeMarks: 1,perQuestionScore: 1, totalQuestionCount: 1,createDateTime:1, pattern:-1,paper:-1}")
	public List<QuizPaperCollection> findByIdListIns(List<String> ids) throws Exception;
	
	@Query(value = "{'$and':[{'paperType':?0},{'paperCategory':?1}]}",
			fields = "{ paperName : 1, _id : -1, id : -1, paperType : 1, paperCategory : 1, paperSubCategory : 1, testType: 1, paperSubCategoryName: 1, totalScore: 1,totalTime: 1,negativeMarks: 1,perQuestionScore: 1, totalQuestionCount: 1,createDateTime:1,dateOfExam:1,dateOfExamYear:1,shiftOfExam:1,cutOffMark:1, pattern:-1,paper:-1}")
	public List<QuizPaperCollection> findByPaperTypeAndPaperCategory(PaperType paperType,PaperCategory paperCategory);

	
}
