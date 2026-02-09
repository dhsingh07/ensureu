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
import com.book.ensureu.model.FreePaper;


@Repository
public interface FreePaperRepository extends MongoRepository<FreePaper, Long>{

	public Optional<FreePaper> findById(Long id);
	
    public List<FreePaper> findByUserId(String userId);
    public List<FreePaper> findByUserIdAndPaperType(String userId,PaperType paperType);
    public List<FreePaper> findByUserIdAndPaperTypeAndTestType(String userId,PaperType paperType,TestType testType);
    
    public List<FreePaper> findByPaperId(String userId);
    public FreePaper findByUserIdAndPaperId(String userId,String freePaperId);
    public long countByTestType(String testType);
    
    @Query(value="{'$and':[{'userId':?0},{'paperId' :{ '$in':?1 }}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,  paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1, totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1,pattern:-1}")
    public List<FreePaper> findFreePaperUsingUserIdAndPaperIds(String userId,List<String> paperIds);
    
    @Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1,  paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1,totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1, pattern:-1}")
    public List<FreePaper> findFreePaperUsingUserIdAndPaperStatus(String userId, PaperStatus paperStatus,PaperType paperType);
    
    @Query(value="{'$and':[{'userId':?0},{'paperStatus' :?1 },{'paperType':?2},{'paperCategory':?3}]}", fields="{ paperName : 1, _id : 1, paperId: 1, paperCategory: 1, paperType : 1, paperSubCategory: 1, testType: 1, paperSubCategoryName: 1, PaperStatus: 1, userId: 1, totalAttemptedQuestionCount:1,totalSkipedCount:1,totalCorrectCount:1,totalInCorrectCount:1,paperValidityStartDate:1,paperValidityEndDate:1,startTestTime: 1,endTestTime: 1,totalScore: 1,totalGetScore:1,createDateTime:1,totalTimeTaken: 1,totalTime: 1,negativeMarks: 1, perQuestionScore:1,totalQuestionCount:1,pattern:-1}")
    public List<FreePaper> findFreePaperUsingUserIdAndPaperStatusAndPaperCategory(String userId, PaperStatus paperStatus,PaperType paperType,PaperCategory paperCategory);

	
}
