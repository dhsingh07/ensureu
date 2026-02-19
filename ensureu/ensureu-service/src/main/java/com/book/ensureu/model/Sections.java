package com.book.ensureu.model;

import java.util.List;

import com.book.ensureu.constant.SectionType;

public class Sections<SB extends SubSections<Question<Problem>>> {

	private String id;
	private String title;
	private int SNo;
	private long minTimeInSecond;
	private long maxTimeInSecond;
	private int questionCount;
	private int skipedCount;
	private int correctCount;
	private int inCorrectCount;
	private SectionType sectionType;
	private QuestionData<Question<Problem>> questionData;
	private List<SB> subSections;
	private double score;
	private double scoreInSection;
	private double perQuestionMarks;
	private double negativeMarks; 
	private long timeTakenSecond;
	private long totalTime;

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

	public long getTimeTakenSecond() {
		return timeTakenSecond;
	}

	public void setTimeTakenSecond(long timeTakenSecond) {
		this.timeTakenSecond = timeTakenSecond;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public QuestionData<Question<Problem>> getQuestionData() {
		return questionData;
	}

	public void setQuestionData(QuestionData<Question<Problem>> questionData) {
		this.questionData = questionData;
	}

	public SectionType getSectionType() {
		return sectionType;
	}

	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}

	
	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
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

	public List<SB> getSubSections() {
		return subSections;
	}

	public void setSubSections(List<SB> subSections) {
		this.subSections = subSections;
	}

	public double getScoreInSection() {
		return scoreInSection;
	}

	public void setScoreInSection(double scoreInSection) {
		this.scoreInSection = scoreInSection;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public double getPerQuestionMarks() {
		return perQuestionMarks;
	}

	public void setPerQuestionMarks(double perQuestionMarks) {
		this.perQuestionMarks = perQuestionMarks;
	}

	public double getNegativeMarks() {
		return negativeMarks;
	}

	public void setNegativeMarks(double negativeMarks) {
		this.negativeMarks = negativeMarks;
	}

	@Override
	public String toString() {
		return "Sections [title=" + title + ", SNo=" + SNo + ", minTimeInSecond=" + minTimeInSecond
				+ ", maxTimeInSecond=" + maxTimeInSecond + ", questionCount=" + questionCount + ", skipedCount="
				+ skipedCount + ", correctCount=" + correctCount + ", inCorrectCount=" + inCorrectCount
				+ ", sectionType=" + sectionType + ", subSections=" + subSections + ", score=" + score
				+ ", scoreInSection=" + scoreInSection + ", perQuestionMarks=" + perQuestionMarks + ", negativeMarks="
				+ negativeMarks + ", timeTakenSecond=" + timeTakenSecond + ", totalTime=" + totalTime + "]";
	}
	
}
