package com.book.ensureu.admin.dto;

import java.util.List;

import com.book.ensureu.admin.constant.DifficultyLevel;
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
public class QuestionBankCreateDto {

    // Taxonomy (required)
    private PaperType paperType;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private String subject;
    private String topic;
    private String subTopic;  // optional

    // Question Content (required)
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

    // Tags & Search
    private List<String> tags;
    private Integer year;
    private String source;

    // Action: DRAFT or PENDING_REVIEW
    private Boolean submitForReview;
}
