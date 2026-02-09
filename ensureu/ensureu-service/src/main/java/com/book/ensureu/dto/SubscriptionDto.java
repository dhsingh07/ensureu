package com.book.ensureu.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.model.Subscription;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SubscriptionDto {
 
    private Long id;

    private List<SubscriptionType> listOfSubscriptionType;
    
	private Long validity;
	
	private List<String> paperIds;
	
	private String paperType;
	
	private String testType;
	
	private String paperCategory;
	
	private String paperSubCategory;
	
	private String description;
	
	private Map<SubscriptionType, PriceMetaDataDto> mapOfSubTypeVsPrice;
	
	private List<PaperInfo> paperInfoList;
	
	private List<Long> listOfSubscriptionIds;

	private Date crDate;

	private Subscription.SubscriptionState state;


}
