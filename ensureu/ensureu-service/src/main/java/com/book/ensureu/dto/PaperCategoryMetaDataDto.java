package com.book.ensureu.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperCategoryMetaDataDto<T> extends PaperCategoryDto {

	private List<T> paperSubCategory;

	
	public PaperCategoryMetaDataDto() {
		super();
	}

	public PaperCategoryMetaDataDto(List<T> paperSubCategory, String paperCategory) {
		super(paperCategory);
		this.paperSubCategory = paperSubCategory;
	}

	public List<T> getpaperSubCategory() {
		return paperSubCategory;
	}

	@Override
	public String toString() {
		return "PaperCategoryMetaDataDto [paperSubCategory=" + paperSubCategory + "]";
	}
	
	
}
