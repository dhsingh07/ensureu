package com.book.ensureu.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.service.PaperService;

public class PaperAdminFactory {

	@Autowired
	@Qualifier("freePaper")
	private PaperService freePaperService;
	
	@Autowired
	@Qualifier("paidPaper")
	private PaperService paidPaperService;
	
	@Autowired
	@Qualifier("pastPaper")
	private PaperService pastPaperService;
	
	@Autowired
	@Qualifier("quizPaper")
	private PaperService quizPaperService;
	
	
	public  PaperService getPaperService(String typePaper) {
		if(TestType.PAID.toString().equalsIgnoreCase(typePaper)) {
			return paidPaperService;
		}else if(TestType.FREE.toString().equalsIgnoreCase(typePaper)) {
			return freePaperService;
		}else if(TestType.PASTPAPER.toString().equalsIgnoreCase(typePaper)) {
			return pastPaperService;
		}else if(TestType.QUIZ.toString().equalsIgnoreCase(typePaper)) {
			return quizPaperService;
		}
		else {
			return null;
		}
	}
	
}
