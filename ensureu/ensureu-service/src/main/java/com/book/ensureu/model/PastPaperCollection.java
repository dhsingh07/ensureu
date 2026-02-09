package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

@Document(collection ="pastPaperCollection")
public class PastPaperCollection extends Paper<Sections<SubSections<Question<Problem>>>> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2493904467309272680L;
	private String id;
	private String paperHashKey;
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
	
	private long dateOfExam;
	private String dateOfExamYear;
	private String shiftOfExam;
	private double cutOffMark;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPaperHashKey() {
		return paperHashKey;
	}
	public void setPaperHashKey(String paperHashKey) {
		this.paperHashKey = paperHashKey;
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
	
	public long getDateOfExam() {
		return dateOfExam;
	}
	public void setDateOfExam(long dateOfExam) {
		this.dateOfExam = dateOfExam;
	}
	public String getDateOfExamYear() {
		return dateOfExamYear;
	}
	public void setDateOfExamYear(String dateOfExamYear) {
		this.dateOfExamYear = dateOfExamYear;
	}
	public String getShiftOfExam() {
		return shiftOfExam;
	}
	public void setShiftOfExam(String shiftOfExam) {
		this.shiftOfExam = shiftOfExam;
	}
	public double getCutOffMark() {
		return cutOffMark;
	}
	public void setCutOffMark(double cutOffMark) {
		this.cutOffMark = cutOffMark;
	}
	
	@Override
	public String toString() {
		return "PastPaperCollection [id=" + id + ", paperHashKey=" + paperHashKey + ", paperType=" + paperType
				+ ", paperSubCategory=" + paperSubCategory + ", paperCategory=" + paperCategory + ", testType="
				+ testType + ", paperName=" + paperName + ", paperSubCategoryName=" + paperSubCategoryName
				+ ", createDateTime=" + createDateTime + ", validityRangeStartDateTime=" + validityRangeStartDateTime
				+ ", validityRangeEndDateTime=" + validityRangeEndDateTime + ", totalQuestionCount="
				+ totalQuestionCount + ", totalScore=" + totalScore + ", totalGetScore=" + totalGetScore
				+ ", negativeMarks=" + negativeMarks + ", perQuestionScore=" + perQuestionScore + ", totalTime="
				+ totalTime + ", totalTakenTime=" + totalTakenTime + ", dateOfExam=" + dateOfExam + ", dateOfExamYear="
				+ dateOfExamYear + ", shiftOfExam=" + shiftOfExam + ", cutOffMark=" + cutOffMark + "]";
	}

	
	
	
}
