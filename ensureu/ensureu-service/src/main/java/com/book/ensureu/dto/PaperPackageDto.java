package com.book.ensureu.dto;

import java.util.List;

public class PaperPackageDto {


	private List<PaperTypeDto> listOfPaperTypeDto;
    private List<SubscriptionDto> listOfFreeSubscription;

	/**
	 * @return the listOfPaperTypeDto
	 */
	public List<PaperTypeDto> getListOfPaperTypeDto() {
		return listOfPaperTypeDto;
	}

	/**
	 * @param listOfPaperTypeDto the listOfPaperTypeDto to set
	 */
	public void setListOfPaperTypeDto(List<PaperTypeDto> listOfPaperTypeDto) {
		this.listOfPaperTypeDto = listOfPaperTypeDto;
	}

	/**
	 * @return the listOfFreeSubscription
	 */
	public List<SubscriptionDto> getListOfFreeSubscription() {
		return listOfFreeSubscription;
	}

	/**
	 * @param listOfFreeSubscription the listOfFreeSubscription to set
	 */
	public void setListOfFreeSubscription(List<SubscriptionDto> listOfFreeSubscription) {
		this.listOfFreeSubscription = listOfFreeSubscription;
	}



}
