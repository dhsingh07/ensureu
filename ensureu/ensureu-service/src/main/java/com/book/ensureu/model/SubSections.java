package com.book.ensureu.model;

import com.book.ensureu.constant.SectionType;

public class SubSections<Q extends Question<Problem>> {

	private String title;
	private int SNo;
	private long minTimeInSecond;
	private long maxTimeInSecond;
	private int questionCount;
	private int skipedCount;
	private int correctCount;
	private int inCorrectCount;
	private SectionType sectionType;
	private QuestionData<Q> questionData;
	private double score;
	private double scoreInSubSection;
	private long timeTakenSecond;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getSNo() {
		return SNo;
	}
	public void setSNo(int sNo) {
		SNo = sNo;
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
	public int getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}
	public int getSkipedCount() {
		return skipedCount;
	}
	public void setSkipedCount(int skipedCount) {
		this.skipedCount = skipedCount;
	}
	public int getCorrectCount() {
		return correctCount;
	}
	public void setCorrectCount(int correctCount) {
		this.correctCount = correctCount;
	}
	public int getInCorrectCount() {
		return inCorrectCount;
	}
	public void setInCorrectCount(int inCorrectCount) {
		this.inCorrectCount = inCorrectCount;
	}
	public SectionType getSectionType() {
		return sectionType;
	}
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}
	public QuestionData<Q> getQuestionData() {
		return questionData;
	}
	public void setQuestionData(QuestionData<Q> questionData) {
		this.questionData = questionData;
	}
	public long getTimeTakenSecond() {
		return timeTakenSecond;
	}
	public void setTimeTakenSecond(long timeTakenSecond) {
		this.timeTakenSecond = timeTakenSecond;
	}
	
	
	public double getScoreInSubSection() {
		return scoreInSubSection;
	}
	public void setScoreInSubSection(double scoreInSubSection) {
		this.scoreInSubSection = scoreInSubSection;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	@Override
	public String toString() {
		return "SubSections [title=" + title + ", SNo=" + SNo + ", minTimeInSecond=" + minTimeInSecond
				+ ", maxTimeInSecond=" + maxTimeInSecond + ", questionCount=" + questionCount + ", skipedCount="
				+ skipedCount + ", correctCount=" + correctCount + ", inCorrectCount=" + inCorrectCount
				+ ", sectionType=" + sectionType + ", questionData=" + questionData + ", score=" + score
				+ ", timeTakenSecond=" + timeTakenSecond + "]";
	}
	
	
	
}
