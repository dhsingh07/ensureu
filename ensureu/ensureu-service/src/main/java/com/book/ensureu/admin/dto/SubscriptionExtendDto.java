package com.book.ensureu.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for extending subscription validity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionExtendDto {

    /**
     * Number of days to extend (mutually exclusive with newValidity)
     * Quick extend options: 7, 30, 90, 365
     */
    private Integer extendDays;

    /**
     * New validity timestamp (mutually exclusive with extendDays)
     * Use for setting specific expiration date
     */
    private Long newValidity;

    /**
     * Optional reason for extension (for audit trail)
     */
    private String reason;
}
