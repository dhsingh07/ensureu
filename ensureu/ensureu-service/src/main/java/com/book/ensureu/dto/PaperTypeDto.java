package com.book.ensureu.dto;

import java.util.List;

public class PaperTypeDto {

	private String paperType;
	
	private List<PaperCategoryDto> listOfCategoryDto;

	/**
	 * @return the paperType
	 */
	public String getPaperType() {
		return paperType;
	}

	/**
	 * @param paperType the paperType to set
	 */
	public void setPaperType(String paperType) {
		this.paperType = paperType;
	}

	/**
	 * @return the listOfCategoryDto
	 */
	public List<PaperCategoryDto> getListOfCategoryDto() {
		return listOfCategoryDto;
	}

	/**
	 * @param listOfCategoryDto the listOfCategoryDto to set
	 */
	public void setListOfCategoryDto(List<PaperCategoryDto> listOfCategoryDto) {
		this.listOfCategoryDto = listOfCategoryDto;
	}
	
	

	
}
