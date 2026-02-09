package com.book.ensureu.dto;

import java.util.List;

import com.book.ensureu.constant.PaperType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaperMetaDataPaperTypeVsPaperDto {
	
	private PaperType paperType;
	List<PaperMetaDataPaperCategoryDto> paperCategory;
	
	public PaperMetaDataPaperTypeVsPaperDto(PaperType paperType, List<PaperMetaDataPaperCategoryDto> paperCategory) {
		super();
		this.paperType = paperType;
		this.paperCategory = paperCategory;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public List<PaperMetaDataPaperCategoryDto> getPaperCategory() {
		return paperCategory;
	}
}
