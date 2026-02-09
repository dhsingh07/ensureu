package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.QuestionVaultDto;

public interface VaultService {

	public void saveQuestion(QuestionVaultDto question,String userName);

	public List<QuestionVaultDto> getQuestionByUserName(String userName);
	
	public List<QuestionVaultDto> getQuestionByUserNameAndPaperType(String userName,PaperType paperType);
	
	public void deleteQuestionByUserNameAndQuestionId(String questionId, String userName);
}
