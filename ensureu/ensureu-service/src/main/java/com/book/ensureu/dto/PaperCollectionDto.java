package com.book.ensureu.dto;

import java.io.Serializable;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Pattern;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;
import com.book.ensureu.model.Sections;
import com.book.ensureu.model.SubSections;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperCollectionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4494973565121010450L;
	private String id;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private String paperSubCategoryName;
	private TestType testType;
	private String paperName;
	private Pattern<Sections<SubSections<Question<Problem>>>> pattern;
	private int totalQuestionCount;
	private double totalScore;
	private double negativeMarks;
	private double perQuestionScore;
	
	private double totalGetScore;
	private long totalTime;
	private long totalTakenTime;
	private boolean taken;
	private int priorty;
	
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
	/*public Pattern<Sections<Question<Problem>>> getPattern() {
		return pattern;
	}
	public void setPattern(Pattern<Sections<Question<Problem>>> pattern) {
		this.pattern = pattern;
	}*/
	public TestType getTestType() {
		return testType;
	}
	public void setTestType(TestType typePaper) {
		this.testType = typePaper;
	}
	
	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}
	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}	
	
	public PaperCollectionDto() {
	}
	
	public PaperCollectionDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String paperSubCategoryName, TestType testType, String paperName,
			Pattern<Sections<SubSections<Question<Problem>>>> pattern, int totalQuestionCount, double totalScore) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.paperSubCategoryName = paperSubCategoryName;
		this.testType = testType;
		this.paperName = paperName;
		this.pattern = pattern;
		this.totalQuestionCount = totalQuestionCount;
		this.totalScore = totalScore;
	}
	public PaperCategory getPaperCategory() {
		return paperCategory;
	}
	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}
	
	public Pattern<Sections<SubSections<Question<Problem>>>> getPattern() {
		return pattern;
	}
	public void setPattern(Pattern<Sections<SubSections<Question<Problem>>>> pattern) {
		this.pattern = pattern;
	}
	public double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}
	@Override
	public String toString() {
		return "PaperCollectionDto [id=" + id + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ ", paperSubCategory=" + paperSubCategory + ", paperSubCategoryName=" + paperSubCategoryName
				+ ", testType=" + testType + ", paperName=" + paperName + ", pattern=" + pattern
				+ ", totalQuestionCount=" + totalQuestionCount + ", totalScore=" + totalScore + "]";
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
	
	
	
}
