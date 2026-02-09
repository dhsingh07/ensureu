package com.book.ensureu.dto;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.book.ensureu.constant.PaperType;

public class PaperMetaDataModelDto {

	@Id
	private Long id;
	private PaperType paperType;
	private List<PaperCategoryMetaDataDto> paperCategory;

	public PaperMetaDataModelDto() {
		
	}

	public PaperMetaDataModelDto(Long id, PaperType paperType, List<PaperCategoryMetaDataDto> paperCategory) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
	}

	@Override
	public String toString() {
		return "PaperMetaDataModelDto [id=" + id + ", paperType=" + paperType + ", paperCategory=" + paperCategory
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public PaperType getPaperType() {
		return paperType;
	}

	public List<PaperCategoryMetaDataDto> getPaperCategory() {
		return paperCategory;
	}
	
	
	
	
}
