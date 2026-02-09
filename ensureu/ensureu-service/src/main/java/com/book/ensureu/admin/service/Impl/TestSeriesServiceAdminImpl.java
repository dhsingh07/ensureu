package com.book.ensureu.admin.service.Impl;

import com.book.ensureu.admin.dao.TestSeriesAdminDao;
import com.book.ensureu.admin.service.TestSeriesService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.common.dto.TestSeriesDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TestSeriesServiceAdminImpl implements TestSeriesService {


    private TestSeriesAdminDao testSeriesDao;

    @Override
    public List<TestSeriesDto> fetchTestSeries(PaperCategory paperCategory, Date crDate, Date validity) {
        log.debug("[fetchTestSeries] paperCategory {}, crDate {}, validity {}",paperCategory, crDate, validity);
        return testSeriesDao.getTestSeries(paperCategory, crDate, validity);
    }

    @Override
    public void createTestSeries(TestSeriesDto testSeriesDto) {
        log.debug("[createTestSeries] testSeriesDto {}",testSeriesDto);
        testSeriesDao.createTestSeries(testSeriesDto);

    }

    @Override
    public void patchTestSeries(TestSeriesDto testSeriesDto) {
        log.debug("[patchTestSeries] testSeriesDto {}",testSeriesDto);
        testSeriesDao.patchTestSeries(testSeriesDto);
    }
}
