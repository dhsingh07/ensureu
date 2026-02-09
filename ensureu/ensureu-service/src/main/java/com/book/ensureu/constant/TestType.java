package com.book.ensureu.constant;

import java.util.LinkedList;
import java.util.List;

public enum TestType {
FREE,PAID,PRACTICE, QUIZ,PASTPAPER,RECOMMENDED,ALL;
	
	public List<TestType> getAll(){
		List<TestType> listOfTestType = new LinkedList<>();
		return  listOfTestType;
	}
	
	public String toString()
	{
		switch(this) {
		case FREE:
			return "FREE";
		case PAID:
			return "PAID";
		case PRACTICE:
			return "PRACTICE";
		case QUIZ:
			return "QUIZ";
		case PASTPAPER:
			return "PASTPAPER";
		case RECOMMENDED:
			return "RECOMMENDED";
		default:
			throw new IllegalArgumentException("Not valid TestType");
		}
	}
}
