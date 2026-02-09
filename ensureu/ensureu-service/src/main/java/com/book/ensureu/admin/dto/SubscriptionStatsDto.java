package com.book.ensureu.admin.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatsDto {

    // Overall counts
    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long draftSubscriptions;

    // Paper statistics
    private Long totalPapersInSubscriptions;
    private Long availablePapers;

    // User statistics
    private Long totalSubscribers;
    private Double totalRevenue;

    // Expiration alerts
    private Long expiringIn7Days;
    private Long expiringIn30Days;

    // Breakdown by paper type
    private Map<String, PaperTypeStats> byPaperType;

    // Breakdown by test type
    private TestTypeStats freeStats;
    private TestTypeStats paidStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaperTypeStats {
        private Long total;
        private Long active;
        private Long subscribers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestTypeStats {
        private Long total;
        private Long active;
        private Double revenue;
    }
}
