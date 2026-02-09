package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.QuizPaper;

public interface QuizPaperRepository extends MongoRepository<QuizPaper, Long>{

	public List<QuizPaper> findByUserId(String userId);
    public List<QuizPaper> findByUserIdAndPaperType(String userId,PaperType paperType);
    public List<QuizPaper> findByUserIdAndPaperTypeAndTestType(String userId,PaperType paperType,TestType testType);

	public List<QuizPaper> findByPaperId(String paperId);

	public QuizPaper findByUserIdAndPaperId(String userId, String paperId);
	
	public List<QuizPaper> findByUserIdAndPaperIdIn(String userId, List<String> paperIds);
	
	public QuizPaper findByUserIdAndPaperIdAndPayment(String userId, String paperId,boolean payment);
	
	public List<QuizPaper> findByTestTypeAndUserId(String testType,String userId);
	
	public List<QuizPaper> findByUserIdAndPaperStatus(String paperStatus,String userId);
	
	@Query(value="{'$and':[{'userId':?0},{'paperId' :{ '$in':?1 }}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<QuizPaper> findQuizPaperUsingUserIdAndPaperIds(String userId,List<String> paperIds);
	
	@Query(value="{'$and':[{'userId':?0},{'paperStatus':{'$ne':'DONE'}},{'paperId' :{ '$in':?1 }}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<QuizPaper> findQuizPaperUsingUserIdAndPaperIdsAndNotDone(String userId,List<String> paperIds);
	
	@Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<QuizPaper> findQuizPaperUsingUserIdAndPaperStatus(String userId, PaperStatus paperStatus,PaperType paperType);

	@Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2},{'paperCategory':?3}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<QuizPaper> findQuizPaperUsingUserIdAndPaperStatusAndPaperCategory(String userId, PaperStatus paperStatus,PaperType paperType,PaperCategory paperCategory);
	
	@Query(value="{'$and':[{'userId':?0},{'paperType':?1},{'paperCategory' :?2}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1,dateOfExam:1,dateOfExamYear:1,shiftOfExam:1,cutOffMark:1, pattern:-1}")
	public List<QuizPaper> findQuizPaperUserIdAndPaperTypeAndPaperCategory(String userId, PaperType paperType, PaperCategory paperCategory);
	
	
}
