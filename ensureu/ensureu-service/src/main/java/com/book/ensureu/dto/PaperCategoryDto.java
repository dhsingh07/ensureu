package com.book.ensureu.dto;

import java.util.List;

public class PaperCategoryDto {

	private String paperCategory;
	
	private List<PaperSubCatogoryDto> listOfSubCategoryDto;

	
	public PaperCategoryDto() {
		super();
	}
	
	

	public PaperCategoryDto(String paperCategory) {
		super();
		this.paperCategory = paperCategory;
	}



	public PaperCategoryDto(String paperCategory, List<PaperSubCatogoryDto> listOfSubCategoryDto) {
		super();
		this.paperCategory = paperCategory;
		this.listOfSubCategoryDto = listOfSubCategoryDto;
	}

	/**
	 * @return the paperCategory
	 */
	public String getPaperCategory() {
		return paperCategory;
	}

	/**
	 * @param paperCategory the paperCategory to set
	 */
	public void setPaperCategory(String paperCategory) {
		this.paperCategory = paperCategory;
	}

	/**
	 * @return the listOfSubCategoryDto
	 */
	public List<PaperSubCatogoryDto> getListOfSubCategoryDto() {
		return listOfSubCategoryDto;
	}

	/**
	 * @param listOfSubCategoryDto the listOfSubCategoryDto to set
	 */
	public void setListOfSubCategoryDto(List<PaperSubCatogoryDto> listOfSubCategoryDto) {
		this.listOfSubCategoryDto = listOfSubCategoryDto;
	}



	@Override
	public String toString() {
		return "PaperCategoryDto [paperCategory=" + paperCategory + ", listOfSubCategoryDto=" + listOfSubCategoryDto
				+ "]";
	}
	
	
	
	
}
