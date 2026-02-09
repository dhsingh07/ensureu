package com.book.ensureu.flow.analytics.model;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserQuestionTime {

    @NotNull
    private String questionId;

    @NotNull
    private int questionNo;

    @NotNull
    private long timeTaken;

    private QuestionAttemptedStatus questionAttemptedStatus;
}
