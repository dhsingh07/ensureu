package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.AttemptedPaperDto;
import com.book.ensureu.dto.PaperDto;

public interface PaperService {
	
public void savePaper(PaperDto paperDto);
public void updatePaper(PaperDto paperDto);

public void submitPaper(AttemptedPaperDto attemptedPaper);
public PaperDto getPaperById(Long id, TestType typePaper);

public List<PaperDto> getPaperByPaperId(String paperId, TestType typePaper);

public PaperDto getPaperByPaperIdAndUserId(String paperId, String userId,TestType typePaper);


//find userId all test
public List<PaperDto> getPaperByUserId(String userId);
public List<PaperDto> getPaperByUserIdAndPaperType(String userId, PaperType paperType);
public List<PaperDto> getPaperByUserIdAndPaperTypeAndTestType(String userId, PaperType paperType,TestType testType);


//search paper on collection or userPaperColletion based on PaperStatus
public PaperDto paperMappedUserByPaperStatus(String userId,TestType testType,PaperStatus paperStatus,String paperId);


//count services for paper type,paper category and paper subcategory...
public long getAllCountPaper();
public long getPaperCountByPaperTypeAndTestType(PaperType paperType, TestType testType);
public long getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType paperType, TestType testType,PaperCategory paperCategory);
public long getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType, TestType testType,PaperCategory paperCategory,PaperSubCategory paperSubCategory);


public long getPaperCountByPaperType(PaperType paperType);

public long getAllCountPaperCollectionByPaperType(PaperType paperType,TestType Paper);




//get paper test status details.
public List<PaperDto> getPaperStatusDetailsByPaperIds(String userId,List<String> paperIds, TestType testType) throws Exception;
//get paper test Status and  paper category
public List<PaperDto> getPaperStatusDetailsByStatusAndPaperType(String userId, TestType testType,PaperType paperType,PaperStatus paperStatus,PaperCategory paperCategory) throws Exception;

// get missded paper for user by paperCategory...
public List<PaperDto> getMissedPapersByUsers(String userId, TestType testType,PaperType paperType,PaperCategory paperCategory) throws Exception;

//Get paperstatus for free and paid...
public List<PaperDto> getPaperStatusDetailsByPaperCateoryORTestType(String userId, TestType testType,PaperType paperType,PaperCategory paperCategory) throws Exception;



}
