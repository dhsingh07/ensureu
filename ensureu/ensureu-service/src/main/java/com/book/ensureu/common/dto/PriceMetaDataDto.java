package com.book.ensureu.common.dto;

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
public class PriceMetaDataDto {

	private Long id;
	
    private Double price;
 	
	private Double pricePerPaper;
	
	private Double discountedPrice;
	
	private Double discountedPricePerPaper;
	
	private Double discountPercentage;
	
	private int minPaperCount;
	
	private int extraPaperCount;
	
	private int totalPaperCount;

	private int numberOfPapers;

	private Long validity;

	private PaperType paperType;

	private PaperCategory paperCategory;

	private PaperSubCategory paperSubCategory;

	private SubscriptionType subscriptionType;

}
