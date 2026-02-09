package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperDetailsDto {

	private Long id;
	private Long PaperId;
	private PaperType paperType;
	private PaperStatus paperStatus;
	private int totalAttemptedQuestionCount;
	private String paperSubCategoryName;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	private String paperName;
	
	public PaperDetailsDto(Long id, Long paperId, PaperType paperType, PaperStatus paperStatus,
			int totalAttemptedQuestionCount, String paperSubCategoryName, PaperSubCategory paperSubCategory,
			TestType testType, String paperName) {
		super();
		this.id = id;
		PaperId = paperId;
		this.paperType = paperType;
		this.paperStatus = paperStatus;
		this.totalAttemptedQuestionCount = totalAttemptedQuestionCount;
		this.paperSubCategoryName = paperSubCategoryName;
		this.paperSubCategory = paperSubCategory;
		this.testType = testType;
		this.paperName = paperName;
	}

	public Long getId() {
		return id;
	}

	public Long getPaperId() {
		return PaperId;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public PaperStatus getPaperStatus() {
		return paperStatus;
	}

	public int getTotalAttemptedQuestionCount() {
		return totalAttemptedQuestionCount;
	}

	public String getPaperSubCategoryName() {
		return paperSubCategoryName;
	}

	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}

	public TestType getTestType() {
		return testType;
	}

	public String getPaperName() {
		return paperName;
	}
	
	
	
}
