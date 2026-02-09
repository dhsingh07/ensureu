package com.book.ensureu.flow.analytics.transformer;

import com.book.ensureu.flow.analytics.dto.UserPaperTimeSeriesDto;
import com.book.ensureu.flow.analytics.model.UserPaperTimeSeries;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class UserPaperTimeSeriesTransformer implements Transformer<UserPaperTimeSeries, UserPaperTimeSeriesDto> {

    private UserQuestionTimeTransformer userQuestionTimeTransformer;

    @Override
    public UserPaperTimeSeries toModel(UserPaperTimeSeriesDto from) {

        return UserPaperTimeSeries.builder()
                //.id()  // TODO need to add id
                .paperId(from.getPaperId())
                .userId(from.getUserId())
                .UserQuestionTimeList(userQuestionTimeTransformer.toModel(from.getUserQuestionTimeDtoList()))
                .build();
    }

    @Override
    public List<UserPaperTimeSeries> toModel(List<UserPaperTimeSeriesDto> from) {
        return null;
    }

    @Override
    public UserPaperTimeSeriesDto ToDto(UserPaperTimeSeries from) {

        return UserPaperTimeSeriesDto.builder()
                .paperId(from.getPaperId())
                .userId(from.getUserId())
                .userQuestionTimeDtoList(userQuestionTimeTransformer.toDto(from.getUserQuestionTimeList()))
                .build();
    }

    @Override
    public List<UserPaperTimeSeriesDto> toDto(List<UserPaperTimeSeries> from) {
        return null;
    }

}
