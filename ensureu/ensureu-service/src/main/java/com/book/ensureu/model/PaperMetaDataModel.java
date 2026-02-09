package com.book.ensureu.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.PaperCategoryMetaDataDto;

@Document(collection = "paperMetaData")
public class PaperMetaDataModel {

	@Id
	private Long id;
	private PaperType paperType;
	private List<PaperCategoryMetaDataDto> paperCategory;
	
	public PaperMetaDataModel(Long id, PaperType paperType, List<PaperCategoryMetaDataDto> paperCategory) {
		super();
		this.id = id;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
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
	public void setId(Long id) {
		this.id = id;
	}
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}
	public void setPaperCategory(List<PaperCategoryMetaDataDto> paperCategory) {
		this.paperCategory = paperCategory;
	}
	@Override
	public String toString() {
		return "PaperMetaDataModel [id=" + id + ", paperType=" + paperType + ", paperCategory=" + paperCategory + "]";
	}
	
	
	
}
