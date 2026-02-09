package com.book.ensureu.flow.analytics.model;

import com.book.ensureu.constant.QuestionAttemptedStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document
public class QuestionStat {

    @Id
    private String questionId;
    private String paperId;
    private Long questionNumber;
    private String paperHierarchy;
    private String section;
    private String subSection;
    private String type;


    private String complexityLevel;
    private int categoryScore;

    private Long maxTime;
    private Long minTime;

    Map<QuestionAttemptedStatus,Long> QuestionAttemptedMap;

    private List<String> userIds;
}
