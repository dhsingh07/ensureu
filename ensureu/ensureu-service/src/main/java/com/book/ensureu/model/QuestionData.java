package com.book.ensureu.model;

import java.util.List;

public class QuestionData<Q extends Question<Problem>> {

	private List<Q> questions;

	private String skip;

	public List<Q> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Q> questions) {
		this.questions = questions;
	}

	public String getSkip() {
		return skip;
	}

	public void setSkip(String skip) {
		this.skip = skip;
	}

	@Override
	public String toString() {
		return "QuestionDataSet [questions=" + questions + ", skip=" + skip + "]";
	}
	
	

}
