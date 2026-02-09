package com.book.ensureu.flow.analytics.service;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.flow.analytics.dto.UserAnalyticsDto;

public interface AnalyticsService {

    UserAnalyticsDto getUserAnalytics(String userId, String paperId);

    default public void submitUserAnalysis(){

    }
}
