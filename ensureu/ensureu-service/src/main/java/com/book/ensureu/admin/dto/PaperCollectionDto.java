package com.book.ensureu.admin.dto;

import java.io.Serializable;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Paper;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;
import com.book.ensureu.model.Sections;
import com.book.ensureu.model.SubSections;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PaperCollectionDto extends Paper<Sections<SubSections<Question<Problem>>>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6568949828993492086L;
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
	public long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	public long getTotalTakenTime() {
		return totalTakenTime;
	}
	public void setTotalTakenTime(long totalTakenTime) {
		this.totalTakenTime = totalTakenTime;
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
	public PaperStateStatus getPaperStateStatus() {
		return paperStateStatus;
	}
	public void setPaperStateStatus(PaperStateStatus paperStateStatus) {
		this.paperStateStatus = paperStateStatus;
	}

	public void initializeDatesIfMissing() {
		long now = System.currentTimeMillis();

		// If create date is missing, set to current date
		if (this.createDateTime == null) {
			this.createDateTime = now;
		}

		// If start date is missing, default to create date
		if (this.validityRangeStartDateTime == null) {
			this.validityRangeStartDateTime = this.createDateTime;
		}

		// If end date is missing, default to start date + 6 months
		if (this.validityRangeEndDateTime == null) {
			this.validityRangeEndDateTime = java.util.Date
					.from(java.time.Instant.ofEpochMilli(this.validityRangeStartDateTime)
							.atZone(java.time.ZoneId.systemDefault())
							.plusMonths(6)
							.toInstant())
					.getTime();
		}
	}

	@Override
	public String toString() {
		return "PaperCollectionDto [id=" + id + ", paperType=" + paperType + ", paperSubCategory=" + paperSubCategory
				+ ", paperCategory=" + paperCategory + ", testType=" + testType + ", paperName=" + paperName
				+ ", paperSubCategoryName=" + paperSubCategoryName + ", createDateTime=" + createDateTime
				+ ", validityRangeStartDateTime=" + validityRangeStartDateTime + ", validityRangeEndDateTime="
				+ validityRangeEndDateTime + ", totalQuestionCount=" + totalQuestionCount + ", totalScore=" + totalScore
				+ ", totalGetScore=" + totalGetScore + ", negativeMarks=" + negativeMarks + ", perQuestionScore="
				+ perQuestionScore + ", totalTime=" + totalTime + ", totalTakenTime=" + totalTakenTime + ", taken="
				+ taken + ", priorty=" + priorty + ", paperStateStatus=" + paperStateStatus + "]";
	}
	
	
	
}
