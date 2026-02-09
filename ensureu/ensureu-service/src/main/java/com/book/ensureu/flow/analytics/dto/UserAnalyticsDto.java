package com.book.ensureu.flow.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * This class contains Histogram, Percentile graph,
 * Time Series graph or Speed graph,
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAnalyticsDto {

    //TODO : it consists of 3 graphs only need to add more

    private UserScoreDto userScoreDto;
    private List<PercentileDto> percentileList;
    private List<QuestionSpeedCompDto> timeHistogramList; // TODO need to remove after testing of sectionHistogramList
    private List<SectionHistogramDto> sectionHistogramDtoList;
    private TimeSeriesDto timeSeriesDto;
    private UserGrowthDto userGrowthDto;

    private List<UserScoreDto> topScorerOfThisPaperList;
    private List<UserScoreDto> topScorerOfThisCategoryList;
    private List<UserScoreDto> previousSubmissionsList;



}
