package com.book.ensureu.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.SectionDeatilDto;

@Document(collection="paperInfoDataModel")
public class PaperInfoDataModel {
	
	@Id
	private String id;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	//use for more granual lable.
	private String PaperSubCategory1;
	private String paperName;
	private double score;
	private double negativeMarks;
	private double perQuestionScore;
	private long totalTime;
	private int totalQuestionCount;
	private List<SectionType> sectionTypeList;
	List<SectionDeatilDto> sections;
	private long createDate;
	private long modifiedDate;
	private String createdBy;
	private String modifiedBy;
	private boolean enable;
	private int priority;
	

	
	public PaperInfoDataModel() {
		super();
	}


	public PaperInfoDataModel(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String paperName, double score, double negativeMarks,
			double perQuestionScore, long totalTime, int totalQuestionCount, List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sections = sections;
	}


	public PaperInfoDataModel(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String paperSubCategory1, String paperName, double score,
			double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,List<SectionType> sectionTypeList,
			List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		PaperSubCategory1 = paperSubCategory1;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sectionTypeList=sectionTypeList;
		this.sections = sections;
	}


	public PaperInfoDataModel(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperSubCategory1, String paperName,
			double score, double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,
			List<SectionType> sectionTypeList, List<SectionDeatilDto> sections) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
		PaperSubCategory1 = paperSubCategory1;
		this.paperName = paperName;
		this.score = score;
		this.negativeMarks = negativeMarks;
		this.perQuestionScore = perQuestionScore;
		this.totalTime = totalTime;
		this.totalQuestionCount = totalQuestionCount;
		this.sectionTypeList = sectionTypeList;
		this.sections = sections;
	}

	

	public PaperInfoDataModel(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperSubCategory1, String paperName,
			double score, double negativeMarks, double perQuestionScore, long totalTime, int totalQuestionCount,
			List<SectionType> sectionTypeList, List<SectionDeatilDto> sections, boolean enable, int priority) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
		PaperSubCategory1 = paperSubCategory1;
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

	public void setId(String id) {
		this.id = id;
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

	public String getPaperName() {
		return paperName;
	}

	public void setPaperName(String paperName) {
		this.paperName = paperName;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
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

	public int getTotalQuestionCount() {
		return totalQuestionCount;
	}

	public void setTotalQuestionCount(int totalQuestionCount) {
		this.totalQuestionCount = totalQuestionCount;
	}

	public List<SectionDeatilDto> getSections() {
		return sections;
	}

	public void setSections(List<SectionDeatilDto> sections) {
		this.sections = sections;
	}

	public String getPaperSubCategory1() {
		return PaperSubCategory1;
	}

	public List<SectionType> getSectionTypeList() {
		return sectionTypeList;
	}

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	
	public boolean isEnable() {
		return enable;
	}


	public int getPriority() {
		return priority;
	}


	@Override
	public String toString() {
		return "PaperInfoDataModel [id=" + id + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ ", paperSubCategory=" + paperSubCategory + ", testType=" + testType + ", PaperSubCategory1="
				+ PaperSubCategory1 + ", paperName=" + paperName + ", score=" + score + ", negativeMarks="
				+ negativeMarks + ", perQuestionScore=" + perQuestionScore + ", totalTime=" + totalTime
				+ ", totalQuestionCount=" + totalQuestionCount + ", sectionTypeList=" + sectionTypeList + ", sections="
				+ sections + ", createDate=" + createDate + ", modifiedDate=" + modifiedDate + ", createdBy="
				+ createdBy + ", modifiedBy=" + modifiedBy + "]";
	}

}
