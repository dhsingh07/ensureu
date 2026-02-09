package com.book.ensureu.model;

import com.book.ensureu.constant.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserEntitlement {

	private Long id;

	private String uuid;
	
	private String userId;
	
	private Long subscriptionId;

	private SubscriptionType subscriptionType;
	
	private String paperId;
	
	private String paperName;
	
	private Long validity;
	
	private Long createdDate;
	
	private Boolean active;
	
	private PaperType paperType;
	
	private PaperCategory paperCategory;
	
	private PaperSubCategory paperSubCategory;
	
	private TestType testType;

	private String testSeriesId;

	private EntitlementType entitlementType;

	private Date crDate;

}
