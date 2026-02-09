package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.SectionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SectionHistogramDto {

    private List<QuestionSpeedCompDto> questionSpeedCompDtoList;
    private int totalRightQuestions;
    private int totalWrongQuestions;
    private int totalSkipped;
    private int totalQuestions;
    private double totalMarks;
    private SectionType sectionType;
    private String section;
    private String subSection;
    private String type;

}
