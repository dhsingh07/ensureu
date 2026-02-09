package com.book.ensureu.admin.dto;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for paper selection in subscription management
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperSelectionDto {

    private String id;                          // Unique paper ID
    private String paperName;
    private PaperType paperType;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private TestType testType;
    private String paperSubCategoryName;

    // Paper details
    private Integer totalQuestionCount;
    private Double totalScore;
    private Double negativeMarks;
    private Long totalTime;                     // milliseconds
    private Long totalTimeMinutes;              // computed: totalTime / 60000
    private PaperStateStatus paperStateStatus;
    private Long createDateTime;

    // Selection state
    private Boolean taken;
    private Boolean isSelected;                 // For edit mode - already in subscription
    private String takenBySubscriptionId;       // If taken, which subscription
    private String takenBySubscriptionName;     // Subscription name for display

    /**
     * Compute time in minutes from milliseconds
     */
    public Long getTotalTimeMinutes() {
        if (totalTime != null && totalTime > 0) {
            return totalTime / 60000;
        }
        return 0L;
    }
}
