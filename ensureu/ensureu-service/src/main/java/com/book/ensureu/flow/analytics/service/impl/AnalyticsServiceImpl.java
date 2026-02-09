package com.book.ensureu.flow.analytics.service.impl;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.flow.analytics.dto.UserAnalyticsDto;
import com.book.ensureu.flow.analytics.dao.UserAnalyticsDao;
import com.book.ensureu.flow.analytics.service.AnalyticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private UserAnalyticsDao userAnalyticsDao;

    @Override
    public UserAnalyticsDto getUserAnalytics(String userId, String paperId) {

        return userAnalyticsDao.buildUserAnalytics(userId, paperId);

    }
}
