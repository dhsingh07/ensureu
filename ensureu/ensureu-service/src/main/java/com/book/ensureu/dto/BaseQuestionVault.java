package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class BaseQuestionVault {

	private String id;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;

	public BaseQuestionVault() {
		super();
	}

	public BaseQuestionVault(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
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

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}

}
