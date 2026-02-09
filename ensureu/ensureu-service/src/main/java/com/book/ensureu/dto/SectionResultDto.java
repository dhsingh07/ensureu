package com.book.ensureu.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class SectionResultDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3174742520652811594L;
	private int sNo;
	private long timeTakenInSecond;
	private int questionCount;
	private int skipedCount;
	private int correctCount;
	private int inCorrectCount;
	private Double score;
	private long timeTakenSecond;
	
	private List<QuestionResultDto> questionVsQuestionRes=new ArrayList<>();

	public SectionResultDto(int sNo, long timeTakenInSecond, int questionCount, int skipedCount, int correctCount,
			int inCorrectCount, Double score, long timeTakenSecond, List<QuestionResultDto> questionVsQuestionRes) {
		super();
		this.sNo = sNo;
		this.timeTakenInSecond = timeTakenInSecond;
		this.questionCount = questionCount;
		this.skipedCount = skipedCount;
		this.correctCount = correctCount;
		this.inCorrectCount = inCorrectCount;
		this.score = score;
		this.timeTakenSecond = timeTakenSecond;
		this.questionVsQuestionRes = questionVsQuestionRes;
	}

	public int getsNo() {
		return sNo;
	}

	public long getTimeTakenInSecond() {
		return timeTakenInSecond;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public int getSkipedCount() {
		return skipedCount;
	}

	public int getCorrectCount() {
		return correctCount;
	}

	public int getInCorrectCount() {
		return inCorrectCount;
	}

	public Double getScore() {
		return score;
	}

	public long getTimeTakenSecond() {
		return timeTakenSecond;
	}

	public List<QuestionResultDto> getQuestionVsQuestionRes() {
		return questionVsQuestionRes;
	}
	
	
	
}
