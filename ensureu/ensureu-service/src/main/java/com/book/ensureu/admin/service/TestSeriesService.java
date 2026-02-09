package com.book.ensureu.admin.service;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.common.dto.TestSeriesDto;

import java.util.Date;
import java.util.List;

public interface TestSeriesService {

    List<TestSeriesDto> fetchTestSeries(PaperCategory paperCategory, Date crDate, Date validity);

    void createTestSeries(TestSeriesDto testSeriesDto);

    void patchTestSeries(TestSeriesDto testSeriesDto);
}
