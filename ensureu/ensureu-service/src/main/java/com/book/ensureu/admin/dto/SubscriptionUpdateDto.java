package com.book.ensureu.admin.dto;

import java.util.List;
import java.util.Map;

import com.book.ensureu.constant.SubscriptionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing subscription
 * Only includes fields that can be modified after creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionUpdateDto {

    // Content (modifiable)
    private String name;
    private String description;

    // Papers (modifiable - respecting uniqueness constraint)
    private List<String> paperIds;

    // Validity (modifiable)
    private Long createdDate;                   // Can modify start date if not yet started
    private Long validity;                      // Can modify expiration

    // Pricing (modifiable)
    private Map<SubscriptionType, PriceMetadataDto> pricing;
}
