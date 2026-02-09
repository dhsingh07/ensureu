package com.book.ensureu.admin.dto;

import java.util.List;
import java.util.Map;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Subscription.SubscriptionState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCreateDto {

    // Classification (required)
    private PaperType paperType;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private TestType testType;

    // Content
    private String name;                        // Required - display name
    private String description;                 // Optional

    // Papers (at least 1 required)
    private List<String> paperIds;

    // Validity
    private Long createdDate;                   // Activation start timestamp
    private Long validity;                      // Expiration timestamp

    // Pricing (required for PAID)
    private Map<SubscriptionType, PriceMetadataDto> pricing;

    // State
    private SubscriptionState state;            // DRAFT or ACTIVE
}
