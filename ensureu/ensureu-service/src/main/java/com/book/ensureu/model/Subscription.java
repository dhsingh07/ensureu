package com.book.ensureu.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperInfo;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document
public class Subscription {

	@Id
	private Long id;

	private Long subscriptionId;

	private Long validity;

	private int amendmentNo;

	private List<String> paperIds;

	private PaperType paperType;

	private TestType testType;

	private PaperCategory paperCategory;

	private PaperSubCategory paperSubCategory;

	// Content
	private String name;                        // Display name (NEW)
	private String description;

	// Timestamps
	private Long createdDate;                   // Activation start date
	private Long activeDate;                    // When state became ACTIVE

	private List<PaperInfo> listOfPaperInfo;

	private SubscriptionState state;

	// Pricing (for PAID subscriptions)
	private Map<SubscriptionType, PriceMetadata> priceMap;

	// Audit fields (NEW)
	private String createdBy;                   // userId of creator
	private String createdByName;               // Display name of creator
	private Long createdAt;                     // Record creation timestamp
	private String updatedBy;                   // userId of last updater
	private Long updatedAt;                     // Last update timestamp

	// Statistics (computed/cached)
	private Integer subscriberCount;
	private Double totalRevenue;

	public enum SubscriptionState {
		 DRAFT,ACTIVE;
	}

	/**
	 * Embedded price metadata for each subscription type
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PriceMetadata {
		private Double originalPrice;
		private Double discountedPrice;
		private Double discountPercentage;
		private Boolean isActive;
	}

}


