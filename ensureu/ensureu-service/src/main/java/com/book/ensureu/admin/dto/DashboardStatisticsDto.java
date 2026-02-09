package com.book.ensureu.admin.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {

    private long totalFreePapers;
    private long totalPaidPapers;
    private long totalPapers;
    private long totalUsers;
    private long activeSubscriptions;

    private Map<String, Long> freePapersByCategory;
    private Map<String, Long> paidPapersByCategory;
    private Map<String, Long> freePapersByState;
    private Map<String, Long> paidPapersByState;
}
