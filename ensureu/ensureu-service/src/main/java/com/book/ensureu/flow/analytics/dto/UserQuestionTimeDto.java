package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserQuestionTimeDto {

    @NotNull
    private String questionId;

    @NotNull
    private int questionNo;

    @NotNull
    private long timeTaken;

    private String section;

    private  String subSection;

    private String type;

    @NotNull
    private QuestionAttemptedStatus questionAttemptedStatus;

    @NotNull
    private Double marks;
}
