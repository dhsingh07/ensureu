package com.book.ensureu.common.model;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceMetaData {


	public PriceMetaData(Long id, Double price, Double pricePerPaper, Double discountedPrice,
			Double discountedPricePerPaper, Double discountPercentage, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, SubscriptionType subscriptionType) {
		this.id = id;
		this.price = price;
		this.pricePerPaper = pricePerPaper;
		this.discountedPrice = discountedPrice;
		this.discountedPricePerPaper = discountedPricePerPaper;
		this.discountPercentage = discountPercentage;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.subscriptionType = subscriptionType;
	}
	


	public PriceMetaData(Long id, Double price, Double pricePerPaper, Double discountedPrice,
			Double discountedPricePerPaper, Double discountPercentage, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, SubscriptionType subscriptionType, int numberOfPapers) {
		this.id = id;
		this.price = price;
		this.pricePerPaper = pricePerPaper;
		this.discountedPrice = discountedPrice;
		this.discountedPricePerPaper = discountedPricePerPaper;
		this.discountPercentage = discountPercentage;
		this.paperType = paperType;
		this.paperCategory = paperCategory;
		this.paperSubCategory = paperSubCategory;
		this.subscriptionType = subscriptionType;
		this.numberOfPapers = numberOfPapers;
	}


	private Long id;
	
    private Double price;
	
	private Double pricePerPaper;
	
	private Double discountedPrice;
	
	private Double discountedPricePerPaper;
	
	private Double discountPercentage;
	
	private PaperType paperType;
	
	private PaperCategory paperCategory;
	
	private PaperSubCategory paperSubCategory;
	
	private SubscriptionType subscriptionType;
	
	private int numberOfPapers;

	private int minPaperCount;

	private int extraPaperCount;

	
}
