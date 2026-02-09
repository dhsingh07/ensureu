package com.book.ensureu.admin.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.admin.constant.DifficultyLevel;
import com.book.ensureu.admin.constant.QuestionBankStatus;
import com.book.ensureu.admin.constant.QuestionType;
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
@Document(collection = "questionBank")
public class QuestionBank {

    @Id
    private String id;

    private String questionId;              // Unique identifier (auto-generated: QB-XXXXXX)

    // Taxonomy
    private PaperType paperType;            // SSC, BANK
    private PaperCategory paperCategory;    // SSC_CGL, SSC_CPO, SSC_CHSL, BANK_PO
    private PaperSubCategory paperSubCategory;  // SSC_CGL_TIER1, SSC_CGL_TIER2, etc.
    private String subject;                 // e.g., "General Intelligence and Reasoning"
    private String topic;                   // e.g., "Analogies", "Coding-Decoding"
    private String subTopic;                // Optional: e.g., "Letter Analogies"

    // Question Content
    private QuestionBankProblem problem;

    // Metadata
    private QuestionType questionType;      // SINGLE or MULTIPLE correct answers
    private DifficultyLevel difficultyLevel;
    private Double marks;                   // Default marks for this question
    private Double negativeMarks;           // Negative marking (default 0.25 or 0.5)
    private Integer averageTime;            // Expected time in seconds

    // Media
    private Boolean hasImage;
    private String imageUrl;
    private String imagePosition;           // INLINE, BELOW_QUESTION, IN_OPTIONS

    // Status & Audit
    private QuestionBankStatus status;
    private String createdBy;               // userId of creator (Teacher/Admin)
    private String createdByName;           // Display name
    private Long createdAt;
    private String updatedBy;
    private Long updatedAt;
    private String approvedBy;              // userId of approver (Admin/SuperAdmin)
    private Long approvedAt;
    private String rejectionReason;         // If rejected

    // Usage Tracking
    private Integer usageCount;             // How many papers use this question
    private Long lastUsedAt;
    private List<String> papersUsedIn;      // Array of paper IDs

    // Tags & Search
    private List<String> tags;              // For search: ["algebra", "percentage", "2024"]
    private Integer year;                   // If from previous year paper
    private String source;                  // e.g., "SSC CGL 2023 Shift 1"
}
