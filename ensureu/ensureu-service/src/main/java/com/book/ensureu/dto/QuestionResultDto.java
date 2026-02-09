package com.book.ensureu.dto;

import java.io.Serializable;

import com.book.ensureu.constant.QuestionAttemptedStatus;

public class QuestionResultDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1735362327229331160L;
	private int qNo;
	private int so;
	private long timeTakenInSecond;
	private QuestionAttemptedStatus questionAttemptedStatus;
	
	public int getqNo() {
		return qNo;
	}
	public void setqNo(int qNo) {
		this.qNo = qNo;
	}
	public int getSo() {
		return so;
	}
	public void setSo(int so) {
		this.so = so;
	}
	public long getTimeTakenInSecond() {
		return timeTakenInSecond;
	}
	public void setTimeTakenInSecond(long timeTakenInSecond) {
		this.timeTakenInSecond = timeTakenInSecond;
	}
	public QuestionAttemptedStatus getQuestionAttemptedStatus() {
		return questionAttemptedStatus;
	}
	public void setQuestionAttemptedStatus(QuestionAttemptedStatus questionAttemptedStatus) {
		this.questionAttemptedStatus = questionAttemptedStatus;
	}
	
	
	
}
