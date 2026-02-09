package com.book.ensureu.model;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import com.book.ensureu.constant.QuestionSelectionType;

public class Question<P extends Problem> {

	private String id;

	private Long qNo;
	
	private P problem;
	
	private P problemHindi;

	private String type;

	private String complexityLevel;

	private int complexityScore;
	
	private QuestionAttemptedStatus questionAttemptedStatus;
	
	private QuestionSelectionType questionType;
	
	private long minTimeInSecond;
	
	private long maxTimeInSecond;
	
	private long timeTakenInSecond;
	private long averageTimeSecond;
	
	private double score;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public P getProblem() {
		return problem;
	}

	public void setProblem(P problem) {
		this.problem = problem;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComplexityLevel() {
		return complexityLevel;
	}

	public void setComplexityLevel(String complexityLevel) {
		this.complexityLevel = complexityLevel;
	}

	public int getComplexityScore() {
		return complexityScore;
	}

	public void setComplexityScore(int complexityScore) {
		this.complexityScore = complexityScore;
	}

	public P getProblemHindi() {
		return problemHindi;
	}

	public void setProblemHindi(P problemHindi) {
		this.problemHindi = problemHindi;
	}

	public QuestionAttemptedStatus getQuestionAttemptedStatus() {
		return questionAttemptedStatus;
	}

	public void setQuestionAttemptedStatus(QuestionAttemptedStatus questionAttemptedStatus) {
		this.questionAttemptedStatus = questionAttemptedStatus;
	}
	
	public QuestionSelectionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionSelectionType questionType) {
		this.questionType = questionType;
	}

	public long getMinTimeInSecond() {
		return minTimeInSecond;
	}

	public void setMinTimeInSecond(long minTimeInSecond) {
		this.minTimeInSecond = minTimeInSecond;
	}

	public long getMaxTimeInSecond() {
		return maxTimeInSecond;
	}

	public void setMaxTimeInSecond(long maxTimeInSecond) {
		this.maxTimeInSecond = maxTimeInSecond;
	}

	public long getTimeTakenInSecond() {
		return timeTakenInSecond;
	}

	public void setTimeTakenInSecond(long timeTakenInSecond) {
		this.timeTakenInSecond = timeTakenInSecond;
	}

	public long getAverageTimeSecond() {
		return averageTimeSecond;
	}

	public void setAverageTimeSecond(long averageTimeSecond) {
		this.averageTimeSecond = averageTimeSecond;
	}
	
	public Long getqNo() {
		return qNo;
	}

	public void setqNo(Long qNo) {
		this.qNo = qNo;
	}

	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", problem=" + problem + ", problemHindi=" + problemHindi + ", type=" + type
				+ ", complexityLevel=" + complexityLevel + ", complexityScore=" + complexityScore
				+ ", questionAttemptedStatus=" + questionAttemptedStatus + ", questionType=" + questionType
				+ ", minTimeInSecond=" + minTimeInSecond + ", maxTimeInSecond=" + maxTimeInSecond
				+ ", timeTakenInSecond=" + timeTakenInSecond + ", averageTimeSecond=" + averageTimeSecond + "]";
	}

	
}
