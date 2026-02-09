package com.book.ensureu.service;

import com.book.ensureu.common.dto.TestSeriesDto;

import java.util.List;

public interface TestSeriesService {

     List<TestSeriesDto> getTestSeries(long validity, boolean active);

     String subscribeTestSeries(String userId, String testSeriesId);
}
