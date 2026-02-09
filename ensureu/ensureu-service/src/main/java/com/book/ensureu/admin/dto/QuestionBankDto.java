package com.book.ensureu.admin.dto;

import java.util.List;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.constant.QuestionType;
import com.book.ensureu.admin.model.QuestionBankProblem;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankDto {

    private String id;
    private String questionId;

    // Taxonomy
    private PaperType paperType;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private String subject;
    private String topic;
    private String subTopic;

    // Question Content
    private QuestionBankProblem problem;

    // Metadata
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private Double marks;
    private Double negativeMarks;
    private Integer averageTime;

    // Media
    private Boolean hasImage;
    private String imageUrl;
    private String imagePosition;

    // Status & Audit
    private QuestionBankStatus status;
    private String createdBy;
    private String createdByName;
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
    private String approvedBy;
    private Long approvedAt;
    private String rejectionReason;

    // Usage Tracking
    private Integer usageCount;
    private Long lastUsedAt;
    private List<String> papersUsedIn;

    // Tags & Search
    private List<String> tags;
    private Integer year;
    private String source;
}
