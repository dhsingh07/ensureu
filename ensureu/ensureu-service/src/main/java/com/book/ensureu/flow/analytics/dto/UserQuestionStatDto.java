package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserQuestionStatDto {


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
