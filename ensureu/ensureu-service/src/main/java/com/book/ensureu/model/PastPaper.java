package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

@Document(collection = "pastPaper")
public class PastPaper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8457720849043265994L;
	@Id
	private Long id;
	private String paperId;
	private String paperHashKey;
	private String userId;
	private PaperStatus paperStatus;
	private PastPaperCollection paper;
	private String paperSubCategoryName;
	private String paperName;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	private int totalSkipedCount;
	private int totalCorrectCount;
	private int totalInCorrectCount;
	private int totalAttemptedQuestionCount;
	private boolean payment;
	private Double price;
	private Long paperValidityStartDate;
	private Long paperValidityEndDate;
	private Long startTestTime;
	private Long endTestTime;
	private Long totalTimeTaken;
	private Double totalScore;
	private Double totalGetScore;
	private Long totalTime;
	private double negativeMarks;
	private double perQuestionScore;
	private int totalQuestionCount;
	private Long createDateTime;
	
	//for pastpaper
	private long dateOfExam;
	private String dateOfExamYear;
	private String shiftOfExam;
	private double cutOffMark;
	
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
	public String getPaperHashKey() {
		return paperHashKey;
	}
	public void setPaperHashKey(String paperHashKey) {
		this.paperHashKey = paperHashKey;
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
	public PastPaperCollection getPaper() {
		return paper;
	}
	public void setPaper(PastPaperCollection paper) {
		this.paper = paper;
	}
	public String getPaperSubCategoryName() {
		return paperSubCategoryName;
	}
	public void setPaperSubCategoryName(String paperSubCategoryName) {
		this.paperSubCategoryName = paperSubCategoryName;
	}
	public String getPaperName() {
		return paperName;
	}
	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}
	public PaperCategory getPaperCategory() {
		return paperCategory;
	}
	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}
	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}
	public void setPaperSubCategory(PaperSubCategory paperSubCategory) {
		this.paperSubCategory = paperSubCategory;
	}
	public TestType getTestType() {
		return testType;
	}
	public void setTestType(TestType testType) {
		this.testType = testType;
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
	public int getTotalAttemptedQuestionCount() {
		return totalAttemptedQuestionCount;
	}
	public void setTotalAttemptedQuestionCount(int totalAttemptedQuestionCount) {
		this.totalAttemptedQuestionCount = totalAttemptedQuestionCount;
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
	public Double getTotalGetScore() {
		return totalGetScore;
	}
	public void setTotalGetScore(Double totalGetScore) {
		this.totalGetScore = totalGetScore;
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
	public Long getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
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
		return "PastPaper [id=" + id + ", paperId=" + paperId + ", paperHashKey=" + paperHashKey + ", userId=" + userId
				+ ", paperStatus=" + paperStatus + ", paper=" + paper + ", paperSubCategoryName=" + paperSubCategoryName
				+ ", paperName=" + paperName + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ ", paperSubCategory=" + paperSubCategory + ", testType=" + testType + ", totalSkipedCount="
				+ totalSkipedCount + ", totalCorrectCount=" + totalCorrectCount + ", totalInCorrectCount="
				+ totalInCorrectCount + ", totalAttemptedQuestionCount=" + totalAttemptedQuestionCount + ", payment="
				+ payment + ", price=" + price + ", paperValidityStartDate=" + paperValidityStartDate
				+ ", paperValidityEndDate=" + paperValidityEndDate + ", startTestTime=" + startTestTime
				+ ", endTestTime=" + endTestTime + ", totalTimeTaken=" + totalTimeTaken + ", totalScore=" + totalScore
				+ ", totalGetScore=" + totalGetScore + ", totalTime=" + totalTime + ", negativeMarks=" + negativeMarks
				+ ", perQuestionScore=" + perQuestionScore + ", totalQuestionCount=" + totalQuestionCount
				+ ", createDateTime=" + createDateTime + ", dateOfExam=" + dateOfExam + ", dateOfExamYear="
				+ dateOfExamYear + ", shiftOfExam=" + shiftOfExam + ", cutOffMark=" + cutOffMark + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paperId == null) ? 0 : paperId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PastPaper other = (PastPaper) obj;
		if (paperId == null) {
			if (other.paperId != null)
				return false;
		} else if (!paperId.equals(other.paperId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
}
