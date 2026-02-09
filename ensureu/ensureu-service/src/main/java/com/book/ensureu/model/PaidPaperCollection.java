package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

@Document(collection ="paidPaperCollection")
public class PaidPaperCollection extends Paper<Sections<SubSections<Question<Problem>>>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7423122875693468610L;

	private String id;
	private PaperType paperType;
	private PaperSubCategory paperSubCategory;
	private PaperCategory paperCategory;
	private TestType testType;
	private String paperName;
	private String paperSubCategoryName;
	private Long createDateTime;
	private Long validityRangeStartDateTime;
	private Long validityRangeEndDateTime;
	private int totalQuestionCount;
	private double totalScore;
	private double totalGetScore;
	private double negativeMarks;
	private double perQuestionScore;
	private long totalTime;
	private long totalTakenTime;
	private boolean taken;
	private int priorty;
	private PaperStateStatus paperStateStatus;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}
	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}
	public void setPaperSubCategory(PaperSubCategory paperSubCategory) {
		this.paperSubCategory = paperSubCategory;
	}
	
	public String getPaperName() {
		return paperName;
	}
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	
	public String getPaperSubCategoryName() {
		return paperSubCategoryName;
	}
	public void setPaperSubCategoryName(String paperSubCategoryName) {
		this.paperSubCategoryName = paperSubCategoryName;
	}
	public Long getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Long getValidityRangeStartDateTime() {
		return validityRangeStartDateTime;
	}
	public void setValidityRangeStartDateTime(Long validityRangeStartDateTime) {
		this.validityRangeStartDateTime = validityRangeStartDateTime;
	}
	public Long getValidityRangeEndDateTime() {
		return validityRangeEndDateTime;
	}
	public void setValidityRangeEndDateTime(Long validityRangeEndDateTime) {
		this.validityRangeEndDateTime = validityRangeEndDateTime;
	}
	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}
	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}
	
	
	public PaperCategory getPaperCategory() {
		return paperCategory;
	}
	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}
	public TestType getTestType() {
		return testType;
	}
	public void setTestType(TestType testType) {
		this.testType = testType;
	}
	
	public boolean isTaken() {
		return taken;
	}
	
	public void setTaken(boolean taken) {
		this.taken = taken;
	}
	
	public int getPriorty() {
		return priorty;
	}
	
	public void setPriorty(int priorty) {
		this.priorty = priorty;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	public double getTotalGetScore() {
		return totalGetScore;
	}
	public void setTotalGetScore(double totalGetScore) {
		this.totalGetScore = totalGetScore;
	}
	public long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	public double getNegativeMarks() {
		return negativeMarks;
	}
	public void setNegativeMarks(double negativeMarks) {
		this.negativeMarks = negativeMarks;
	}
	public double getPerQuestionScore() {
		return perQuestionScore;
	}
	public void setPerQuestionScore(double perQuestionScore) {
		this.perQuestionScore = perQuestionScore;
	}
	public long getTotalTakenTime() {
		return totalTakenTime;
	}
	public void setTotalTakenTime(long totalTakenTime) {
		this.totalTakenTime = totalTakenTime;
	}

	
	public PaperStateStatus getPaperStateStatus() {
		return paperStateStatus;
	}
	public void setPaperStateStatus(PaperStateStatus paperStateStatus) {
		this.paperStateStatus = paperStateStatus;
	}
	@Override
	public String toString() {
		return "PaidPaperCollection [id=" + id + ", paperType=" + paperType + ", paperSubCategory=" + paperSubCategory
				+ ", paperCategory=" + paperCategory + ", testType=" + testType + ", paperName=" + paperName
				+ ", paperSubCategoryName=" + paperSubCategoryName + ", createDateTime=" + createDateTime
				+ ", validityRangeStartDateTime=" + validityRangeStartDateTime + ", validityRangeEndDateTime="
				+ validityRangeEndDateTime + ", totalQuestionCount=" + totalQuestionCount + ", totalScore=" + totalScore
				+ ", totalGetScore=" + totalGetScore + ", negativeMarks=" + negativeMarks + ", perQuestionScore="
				+ perQuestionScore + ", totalTime=" + totalTime + ", totalTakenTime=" + totalTakenTime + ", taken="
				+ taken + ", priorty=" + priorty + ", getPattern()=" + getPattern() + ", toString()=" + super.toString()
				+ "]";
	}
	
	
	
}
