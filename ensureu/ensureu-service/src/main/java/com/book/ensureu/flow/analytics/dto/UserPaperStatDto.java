package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.TestType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPaperStatDto {

    private String id;

    private String userId;

    @NotNull
    private String paperId;

    private String paperName;

    @NotNull
    private TestType testType;

    private PaperCategory paperCategory;

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

    private List<UserQuestionStatDto> userQuestionStatList;

    private List<SectionHistogramDto> sectionHistogramDtoList;

}
