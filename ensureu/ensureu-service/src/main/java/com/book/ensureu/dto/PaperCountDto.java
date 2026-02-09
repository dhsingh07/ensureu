package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperCountDto {

	private int count;
	private PaperType paperType;
	private PaperSubCategory paperSubCategory;
	private TestType typePaper;
	
	public PaperCountDto(int count, PaperType paperType, PaperSubCategory paperSubCategory, TestType typePaper) {
		super();
		this.count = count;
		this.paperType = paperType;
		this.paperSubCategory = paperSubCategory;
		this.typePaper = typePaper;
	}

	public int getCount() {
		return count;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}

	public TestType getTypePaper() {
		return typePaper;
	}
	
	
}
