package com.book.ensureu.dto;

import com.book.ensureu.constant.TestType;

public class PaperMetaDataPaperCategoryDto {
	private String paperCategory;
	private String paperSubCategory;
	private long paperSubCategoryCount;
	private long paperCategoryCount;
	private TestType testType;
	public PaperMetaDataPaperCategoryDto(String paperCategory, String paperSubCategory,
			long paperSubCategoryCount, long paperCategoryCount, TestType testType) {
		super();
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.paperSubCategoryCount = paperSubCategoryCount;
		this.paperCategoryCount = paperCategoryCount;
		this.testType = testType;
	}
	public String getPaperCategory() {
		return paperCategory;
	}
	public String getPaperSubCategory() {
		return paperSubCategory;
	}
	public long getPaperSubCategoryCount() {
		return paperSubCategoryCount;
	}
	public long getPaperCategoryCount() {
		return paperCategoryCount;
	}
	public TestType getTestType() {
		return testType;
	}
	
	
}
