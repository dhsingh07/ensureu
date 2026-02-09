package com.book.ensureu.flow.analytics.model;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserQuestionStat {

    private String questionId;
    private Long questionNumber;
    private String section;
    private String subSection;
    private String type;
    private String complexityLevel;
    private String languageLevel;  // TODO need to add enums

    private Long timeTaken;
    private int categoryScore;
    private QuestionAttemptedStatus questionAttemptedStatus;
    private Double marks;

}
