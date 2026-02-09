package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperCollMetadataDto {
	
	private String paperId;
	private PaperType paperType;
	private String userId;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private String paperName;
	private TestType testType;
	private PaperStatus paperStatus;
	
	public PaperCollMetadataDto(String paperId, PaperType paperType, String userId, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String paperName, TestType testType, PaperStatus paperStatus) {
		super();
		this.paperId = paperId;
		this.paperType = paperType;
		this.userId = userId;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.paperName = paperName;
		this.testType = testType;
		this.paperStatus = paperStatus;
	}

	public PaperCollMetadataDto(String paperId, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, String paperName, TestType testType,PaperStatus paperStatus) {
		super();
		this.paperId = paperId;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.paperName = paperName;
		this.testType = testType;
		this.paperStatus = paperStatus;
	}

	public String getPaperId() {
		return paperId;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public String getUserId() {
		return userId;
	}

	public PaperCategory getPaperCategory() {
		return paperCategory;
	}

	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}

	public String getPaperName() {
		return paperName;
	}

	public TestType getTestType() {
		return testType;
	}

	public PaperStatus getPaperStatus() {
		return paperStatus;
	}

	public void setPaperStatus(PaperStatus paperStatus) {
		this.paperStatus = paperStatus;
	}
}
