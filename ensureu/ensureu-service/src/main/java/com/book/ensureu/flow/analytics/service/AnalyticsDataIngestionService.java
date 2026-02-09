package com.book.ensureu.flow.analytics.service;

import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.flow.analytics.dto.UserPaperStatDto;
import com.book.ensureu.flow.analytics.dto.UserPaperTimeSeriesDto;
import com.book.ensureu.flow.analytics.dto.UserQuestionTimeDto;
import com.book.ensureu.flow.analytics.model.UserPaperTimeSeries;

import java.util.List;

public interface AnalyticsDataIngestionService {


    void saveUserPaperStat(UserPaperStatDto userPaperStatDto);

    void saveUserTimeSeries(List<UserQuestionTimeDto> userQuestionTimeDto);

    void saveUserPaperStatFromPaperDto(PaperDto paperDto);

    void saveUsePaperTimeSeries(UserPaperTimeSeriesDto userPaperTimeSeriesDto);

    UserPaperTimeSeriesDto fetchUserPaperTimeSeries(String userId, String paperId);
}
