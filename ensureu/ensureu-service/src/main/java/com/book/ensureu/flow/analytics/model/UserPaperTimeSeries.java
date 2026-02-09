package com.book.ensureu.flow.analytics.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class UserPaperTimeSeries {

    private String id;
    @NotNull
    private String userId;
    @NotNull
    private String paperId;
    @NotNull
    private List<UserQuestionTime> UserQuestionTimeList;
}
