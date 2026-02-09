package com.book.ensureu.flow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * quesTimeMap contains keys as time in milliseconds for quesRateMap
 * where value represent question no.
 *
 * @author manishthakran
 * @since 1 oct 2019
 */

//TODO : this class can also has map for speed per 5 mins
@Builder
@Data
@AllArgsConstructor
public class TimeSeriesDto {

    List<Long> quesTimeList;
    Map<Long, UserQuestionTimeDto> timeVsUserQuestionTimeDtoMap;
    List<UserQuestionTimeDto> userQuestionTimeDtoList;

}
