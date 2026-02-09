package com.book.ensureu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

@Document(collection = "freePaper")
public class FreePaper extends UserPaper implements Comparable<FreePaper>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2160431047866598927L;
	
	@Id
	private Long id;
	private String paperId;
	private String userId;
	private PaperStatus paperStatus;
	private int totalAttemptedQuestionCount;
	private FreePaperCollection paper;
	private int totalSkipedCount;
	private int totalCorrectCount;
	private int totalInCorrectCount;
	private Long createDateTime;
	private Long paperValidityStartDate;
	private Long paperValidityEndDate;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private String paperSubCategoryName;
	private String paperName;
	private TestType testType;
	private Long startTestTime;
	private Long endTestTime;
	private Long totalTimeTaken;
	private Double totalScore;
	private Double totalGetScore;
	private Long totalTime;
	private double negativeMarks;
	private double perQuestionScore;
	private int totalQuestionCount;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaperId() {
		return paperId;
	}

	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public PaperStatus getPaperStatus() {
		return paperStatus;
	}

	public void setPaperStatus(PaperStatus paperStatus) {
		this.paperStatus = paperStatus;
	}

	public int getTotalAttemptedQuestionCount() {
		return totalAttemptedQuestionCount;
	}

	public void setTotalAttemptedQuestionCount(int totalAttemptedQuestionCount) {
		this.totalAttemptedQuestionCount = totalAttemptedQuestionCount;
	}

	public FreePaperCollection getPaper() {
		return paper;
	}

	public void setPaper(FreePaperCollection paper) {
		this.paper = paper;
	}

	public int getTotalSkipedCount() {
		return totalSkipedCount;
	}

	public void setTotalSkipedCount(int totalSkipedCount) {
		this.totalSkipedCount = totalSkipedCount;
	}

	public int getTotalCorrectCount() {
		return totalCorrectCount;
	}

	public void setTotalCorrectCount(int totalCorrectCount) {
		this.totalCorrectCount = totalCorrectCount;
	}

	public int getTotalInCorrectCount() {
		return totalInCorrectCount;
	}

	public void setTotalInCorrectCount(int totalInCorrectCount) {
		this.totalInCorrectCount = totalInCorrectCount;
	}

	public Long getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Long getPaperValidityStartDate() {
		return paperValidityStartDate;
	}

	public void setPaperValidityStartDate(Long paperValidityStartDate) {
		this.paperValidityStartDate = paperValidityStartDate;
	}

	public Long getPaperValidityEndDate() {
		return paperValidityEndDate;
	}

	public void setPaperValidityEndDate(Long paperValidityEndDate) {
		this.paperValidityEndDate = paperValidityEndDate;
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

	public String getPaperSubCategoryName() {
		return paperSubCategoryName;
	}

	public void setPaperSubCategoryName(String paperSubCategoryName) {
		this.paperSubCategoryName = paperSubCategoryName;
	}

	public TestType getTestType() {
		return testType;
	}
	
	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	public Long getStartTestTime() {
		return startTestTime;
	}

	public void setStartTestTime(Long startTestTime) {
		this.startTestTime = startTestTime;
	}

	public Long getEndTestTime() {
		return endTestTime;
	}

	public void setEndTestTime(Long endTestTime) {
		this.endTestTime = endTestTime;
	}

	public Long getTotalTimeTaken() {
		return totalTimeTaken;
	}

	public void setTotalTimeTaken(Long totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	public Double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Double totalScore) {
		this.totalScore = totalScore;
	}

	public String getPaperName() {
		return paperName;
	}

	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}

	public PaperCategory getPaperCategory() {
		return paperCategory;
	}

	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
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

	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}

	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}

	
	public Double getTotalGetScore() {
		return totalGetScore;
	}

	public void setTotalGetScore(Double totalGetScore) {
		this.totalGetScore = totalGetScore;
	}

	@Override
	public String toString() {
		return "FreePaper [id=" + id + ", paperId=" + paperId + ", userId=" + userId + ", paperStatus=" + paperStatus
				+ ", totalAttemptedQuestionCount=" + totalAttemptedQuestionCount + ", paper=" + paper
				+ ", totalSkipedCount=" + totalSkipedCount + ", totalCorrectCount=" + totalCorrectCount
				+ ", totalInCorrectCount=" + totalInCorrectCount + ", createDateTime=" + createDateTime
				+ ", paperValidityStartDate=" + paperValidityStartDate + ", paperValidityEndDate="
				+ paperValidityEndDate + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ ", paperSubCategory=" + paperSubCategory + ", paperSubCategoryName=" + paperSubCategoryName
				+ ", paperName=" + paperName + ", testType=" + testType + ", startTestTime=" + startTestTime
				+ ", endTestTime=" + endTestTime + ", totalTimeTaken=" + totalTimeTaken + ", totalScore=" + totalScore
				+ ", totalGetScore=" + totalGetScore + ", totalTime=" + totalTime + ", negativeMarks=" + negativeMarks
				+ ", perQuestionScore=" + perQuestionScore + ", totalQuestionCount=" + totalQuestionCount + "]";
	}

	@Override
	public int compareTo(FreePaper o) {
		return 0;
	}

}
