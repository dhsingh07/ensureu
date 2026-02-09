package com.book.ensureu.flow.analytics.model;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.flow.analytics.dto.SectionHistogramDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document()
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserPaperStat {

    @Id
    private String id;

    private String userId;

    private String paperId;

    private String paperName;

    private TestType testType;

    private PaperCategory paperCategory;

    private PaperSubCategory paperSubCategory;

    private String paperHierarchy;

    private int totalQuestions;

    private Long totalTime;

    private boolean completed;

    private Double totalScore;

    private Double maxPossibleScore;

    private int totalSkipped;

    private int totalCorrect;

    private int totalAttempted;

    private Long totalTimeTaken;

    private boolean freeze;

    private long createdAt;

    private List<UserQuestionStat> userQuestionStatList;


}
