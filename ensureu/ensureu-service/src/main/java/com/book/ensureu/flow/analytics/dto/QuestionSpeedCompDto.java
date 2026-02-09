package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.SectionType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QuestionSpeedCompDto {

    private String questionId;
    private Long questionNumber;
    private Long timeTakenByUser;
    private Long timeTakenByTopper;
    private double averageTime;
    private double userMarks;
    private double topperMarks;
    private double avgMarks;
    private SectionType sectionType;
    private String section;
    private String subSection;
    private String type;

}
