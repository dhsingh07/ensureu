package com.book.ensureu.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription pricing metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceMetadataDto {

    private Double originalPrice;
    private Double discountedPrice;
    private Double discountPercentage;
    private Boolean isActive;

    /**
     * Calculate discount percentage if not provided
     */
    public Double getDiscountPercentage() {
        if (discountPercentage != null) {
            return discountPercentage;
        }
        if (originalPrice != null && discountedPrice != null && originalPrice > 0) {
            return ((originalPrice - discountedPrice) / originalPrice) * 100;
        }
        return 0.0;
    }
}
