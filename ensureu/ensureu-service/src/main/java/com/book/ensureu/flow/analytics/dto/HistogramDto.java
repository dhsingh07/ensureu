package com.book.ensureu.flow.analytics.dto;

import com.book.ensureu.constant.SectionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class HistogramDto {

    private Map<SectionType, List<SectionHistogramDto>> sectionVsQuestionSpeedCompDtoMap;
    private int rightQuestions;
    private int wrongQuestions;
    private int totalSkipped;
    private int totalQuestions;
    private double totalMarks;

}
