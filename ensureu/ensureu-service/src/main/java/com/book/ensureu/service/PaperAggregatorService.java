package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.PastPaperCollection;
import com.book.ensureu.model.PracticePaperCollection;
import com.book.ensureu.model.QuizPaperCollection;

public interface PaperAggregatorService<T> {

	public void createPaidPaper(List<T> paperContent,String path,String imagePath);
	public void createFreeTypePaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public void createPrecticePaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public void createQuizPaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public void createPastPaper(List<T> paperContent,TestType testType, String path,String imagePath);
	
	
	public PaidPaperCollection getPaidPaper(List<T> paperContent,String path,String imagePath);
	public FreePaperCollection getFreeTypePaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public PracticePaperCollection getPrecticePaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public QuizPaperCollection getQuizPaper(List<T> paperContent,TestType testType, String path,String imagePath);
	public PastPaperCollection getPastPaper(List<T> paperContent,TestType testType, String path,String imagePath);
	
}
