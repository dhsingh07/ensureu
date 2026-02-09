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
 * DTO for subscription details in admin panel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAdminDto {

    private String id;
    private Long subscriptionId;

    // Classification
    private PaperType paperType;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private TestType testType;

    // Content
    private String name;
    private String description;
    private Integer paperCount;
    private List<String> paperIds;
    private List<PaperSelectionDto> papers;

    // Validity
    private Long createdDate;           // Activation start timestamp
    private Long validity;              // Expiration timestamp
    private Integer validityDays;       // Computed remaining days
    private Boolean isExpired;

    // Pricing
    private Map<SubscriptionType, PriceMetadataDto> pricing;

    // State
    private SubscriptionState state;

    // Statistics
    private Integer subscriberCount;
    private Integer activeSubscribers;
    private Double totalRevenue;

    // Audit
    private String createdBy;
    private String createdByName;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
}
