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
public class QuestionBankStatsDto {

    private long totalQuestions;
    private long draftCount;
    private long pendingReviewCount;
    private long approvedCount;
    private long rejectedCount;
    private long archivedCount;

    // Breakdown by subject
    private Map<String, Long> questionsBySubject;

    // Breakdown by difficulty
    private Map<String, Long> questionsByDifficulty;

    // Breakdown by category
    private Map<String, Long> questionsByCategory;

    // User stats (for teachers)
    private Long myTotalQuestions;
    private Long myDraftCount;
    private Long myPendingCount;
    private Long myApprovedCount;
    private Long myRejectedCount;
}
