package com.book.ensureu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.PastPaper;

@Repository
public interface PastPaperRepository extends MongoRepository<PastPaper, Long> {

	public Optional<PastPaper> findById(Long id);

	public List<PastPaper> findByUserId(String userId);
    public List<PastPaper> findByUserIdAndPaperType(String userId,PaperType paperType);
    public List<PastPaper> findByUserIdAndPaperTypeAndTestType(String userId,PaperType paperType,TestType testType);

	public List<PastPaper> findByPaperId(String paperId);

	public PastPaper findByUserIdAndPaperId(String userId, String paperId);
	
	public List<PastPaper> findByUserIdAndPaperIdIn(String userId, List<String> paperIds);
	
	public PastPaper findByUserIdAndPaperIdAndPayment(String userId, String paperId,boolean payment);
	
	public List<PastPaper> findByTestTypeAndUserId(String testType,String userId);
	
	public List<PastPaper> findByUserIdAndPaperStatus(String paperStatus,String userId);
	
	@Query(value="{'$and':[{'userId':?0},{'paperId' :{ '$in':?1 }}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<PastPaper> findPastPaperUsingUserIdAndPaperIds(String userId,List<String> paperIds);
	
	@Query(value="{'$and':[{'userId':?0},{'paperStatus':{'$ne':'DONE'}},{'paperId' :{ '$in':?1 }}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<PastPaper> findPastPaperUsingUserIdAndPaperIdsAndNotDone(String userId,List<String> paperIds);
	
	@Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<PastPaper> findPastPaperUsingUserIdAndPaperStatus(String userId, PaperStatus paperStatus,PaperType paperType);

	@Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2},{'paperCategory':?3}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
	public List<PastPaper> findPastPaperUsingUserIdAndPaperStatusAndPaperCategory(String userId, PaperStatus paperStatus,PaperType paperType,PaperCategory paperCategory);
	
	@Query(value="{'$and':[{'userId':?0},{'paperType':?1},{'paperCategory' :?2}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1,dateOfExam:1,dateOfExamYear:1,shiftOfExam:1,cutOffMark:1, pattern:-1}")
	public List<PastPaper> findPastPaperUserIdAndPaperTypeAndPaperCategory(String userId,PaperType paperType,PaperCategory paperCategory);
	
	
}
