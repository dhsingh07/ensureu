package com.book.ensureu.dto;

import java.io.Serializable;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaperDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6606818522708333880L;
	private Long id;
	private String paperId;
	private String userId;
	private PaperStatus paperStatus;
	private int totalAttemptedQuestionCount;
	private PaperCollectionDto paper;
	
	private String paperSubCategoryName;
	private String paperName;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	
	private int totalSkipedCount;
	private int totalCorrectCount;
	private int totalInCorrectCount;
	private boolean payment;
	private Double price;
	private Long paperValidityStartDate;
	private Long paperValidityEndDate;
	private Long startTestTime;
	private Long endTestTime;
	private Double totalScore;
	private Double totalGetScore;
	private Long createDateTime;
	private Long totalTimeTaken;
	private Long totalTime;
	private double negativeMarks;
	private double perQuestionScore;
	private int totalQuestionCount;
	
	private long dateOfExam;
	private String dateOfExamYear;
	private String shiftOfExam;
	private double cutOffMark;
	
	private double percentile;
	
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

	public PaperCollectionDto getPaper() {
		return paper;
	}

	public void setPaper(PaperCollectionDto paper) {
		this.paper = paper;
	}

	public String getPaperSubCategoryName() {
		return paperSubCategoryName;
	}

	public void setPaperSubCategoryName(String paperSubCategoryName) {
		this.paperSubCategoryName = paperSubCategoryName;
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

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public Double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Double totalScore) {
		this.totalScore = totalScore;
	}

	public Long getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
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

	public Long getTotalTime() {
		return totalTime;
	}


	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}



	public Long getTotalTimeTaken() {
		return totalTimeTaken;
	}


	public void setTotalTimeTaken(Long totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}
	
	public String getPaperName() {
		return paperName;
	}


	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	
	public Double getTotalGetScore() {
		return totalGetScore;
	}

	public void setTotalGetScore(Double totalGetScore) {
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

	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}

	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
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
	
	public double getPercentile() {
		return percentile;
	}

	public void setPercentile(double percentile) {
		this.percentile = percentile;
	}

	@Override
	public String toString() {
		return "PaperDto [id=" + id + ", paperId=" + paperId + ", userId=" + userId + ", paperStatus=" + paperStatus
				+ ", totalAttemptedQuestionCount=" + totalAttemptedQuestionCount + ", paper=" + paper
				+ ", paperSubCategoryName=" + paperSubCategoryName + ", paperName=" + paperName + ", paperType="
				+ paperType + ", paperCategory=" + paperCategory + ", paperSubCategory=" + paperSubCategory
				+ ", testType=" + testType + ", totalSkipedCount=" + totalSkipedCount + ", totalCorrectCount="
				+ totalCorrectCount + ", totalInCorrectCount=" + totalInCorrectCount + ", payment=" + payment
				+ ", price=" + price + ", paperValidityStartDate=" + paperValidityStartDate + ", paperValidityEndDate="
				+ paperValidityEndDate + ", startTestTime=" + startTestTime + ", endTestTime=" + endTestTime
				+ ", totalScore=" + totalScore + ", totalGetScore=" + totalGetScore + ", createDateTime="
				+ createDateTime + ", totalTimeTaken=" + totalTimeTaken + ", totalTime=" + totalTime
				+ ", negativeMarks=" + negativeMarks + ", perQuestionScore=" + perQuestionScore
				+ ", totalQuestionCount=" + totalQuestionCount + ", dateOfExam=" + dateOfExam + ", dateOfExamYear="
				+ dateOfExamYear + ", shiftOfExam=" + shiftOfExam + ", cutOffMark=" + cutOffMark + ", percentile="
				+ percentile + "]";
	}

	
}
