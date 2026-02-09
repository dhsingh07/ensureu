package com.book.ensureu.dto;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;

public class PaperInfoDataDto {

	@Id
	private String id;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	//use for more granual lable.
	private String PaperSubCategoryName;
	private String paperName;
	private double score;
	private double negativeMarks;
	private double perQuestionScore;
	private long totalTime;
	private int totalQuestionCount;
	private List<SectionType> sectionTypeList;
	List<SectionDeatilDto> sections;
	//enable OR disable the paper 
	//tiler1,tier2..based on flag will show the subscription and testPaper etc.
	private boolean enable;
	
	//0,1,2,3 priority..high to low.
	private int priority;
	
	
	
	public PaperInfoDataDto() {
		super();
	}


	public PaperInfoDataDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String PaperSubCategoryName, String paperName, double score,
			double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,
			List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.PaperSubCategoryName = PaperSubCategoryName;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sections = sections;
	}
	
	
	public PaperInfoDataDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String PaperSubCategoryName, String paperName, double score, double negativeMarks,
			double perQuestionScore, long totalTime, int totalQuestionCount, List<SectionType> sectionTypeList, List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.PaperSubCategoryName = PaperSubCategoryName;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sectionTypeList=sectionTypeList;
		this.sections = sections;
	}


	public PaperInfoDataDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperSubCategoryName, String paperName,
			double score, double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,
			List<SectionType> sectionTypeList, List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
		PaperSubCategoryName = paperSubCategoryName;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sectionTypeList = sectionTypeList;
		this.sections = sections;
	}

	

	public PaperInfoDataDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperSubCategoryName, String paperName,
			double score, double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,
			List<SectionType> sectionTypeList, List<SectionDeatilDto> sections, boolean enable, int priority) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
		PaperSubCategoryName = paperSubCategoryName;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sectionTypeList = sectionTypeList;
		this.sections = sections;
		this.enable = enable;
		this.priority = priority;
	}


	public String getId() {
		return id;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public PaperCategory getPaperCategory() {
		return paperCategory;
	}
	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}
	public String getPaperSubCategory1() {
		return PaperSubCategoryName;
	}
	public String getPaperName() {
		return paperName;
	}
	public double getScore() {
		return score;
	}
	public double getNegativeMarks() {
		return negativeMarks;
	}
	public double getPerQuestionScore() {
		return perQuestionScore;
	}
	public long getTotalTime() {
		return totalTime;
	}
	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}
	public List<SectionDeatilDto> getSections() {
		return sections;
	}


	public String getPaperSubCategoryName() {
		return PaperSubCategoryName;
	}


	public List<SectionType> getSectionTypeList() {
		return sectionTypeList;
	}


	public TestType getTestType() {
		return testType;
	}

	public void setId(String id) {
		this.id = id;
	}


	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}


	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}


	public void setPaperSubCategory(PaperSubCategory paperSubCategory) {
		this.paperSubCategory = paperSubCategory;
	}


	public void setTestType(TestType testType) {
		this.testType = testType;
	}


	public void setPaperSubCategoryName(String paperSubCategoryName) {
		PaperSubCategoryName = paperSubCategoryName;
	}


	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}


	public void setScore(double score) {
		this.score = score;
	}


	public void setNegativeMarks(double negativeMarks) {
		this.negativeMarks = negativeMarks;
	}


	public void setPerQuestionScore(double perQuestionScore) {
		this.perQuestionScore = perQuestionScore;
	}


	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}


	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}


	public void setSectionTypeList(List<SectionType> sectionTypeList) {
		this.sectionTypeList = sectionTypeList;
	}


	public void setSections(List<SectionDeatilDto> sections) {
		this.sections = sections;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "PaperInfoDataDto [id=" + id + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ ", paperSubCategory=" + paperSubCategory + ", PaperSubCategoryName=" + PaperSubCategoryName
				+ ", paperName=" + paperName + ", score=" + score + ", negativeMarks=" + negativeMarks
				+ ", perQuestionScore=" + perQuestionScore + ", totalTime=" + totalTime + ", totalQuestionCount="
				+ totalQuestionCount + ", sectionTypeList=" + sectionTypeList + ", sections=" + sections + "]";
	}
	
	
	
	
	
}
