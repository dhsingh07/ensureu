package com.book.ensureu.service.impl;


import com.book.ensureu.constant.*;
import com.book.ensureu.dao.TestSeriesDao;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.exception.unchecked.InvalidRequestException;
import com.book.ensureu.model.PurchaseTestSeries;
import com.book.ensureu.common.model.TestSeries;
import com.book.ensureu.repository.PurchasedTestSeriesRepository;
import com.book.ensureu.repository.TestSeriesRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.TestSeriesService;
import com.book.ensureu.service.UserEntitlementService;
import com.book.ensureu.common.transformer.TestSeriesTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TestSeriesServiceImpl implements TestSeriesService {

    private TestSeriesRepository testSeriesRepository;

    private PurchasedTestSeriesRepository purchasedTestSeriesRepository;

    private CounterService counterService;

    private TestSeriesDao testSeriesDao;

    private UserEntitlementService userEntitlementService;


    @Override
    public List<TestSeriesDto> getTestSeries(long validity, boolean active) {

        log.info("[getTestSeries] fetch for validity : {}", validity);
        List<TestSeries> testSeriesList = testSeriesRepository.findTestSeriesByValidity(validity, active).orElse(null);
        return TestSeriesTransformer.toDTOs(testSeriesList);
    }

    public String subscribeTestSeries(String userId, String testSeriesId) {

        log.info("[subscribeTestSeries] request for userId: {} testSeriesId: {}", userId, testSeriesId);
        testSeriesRepository.findById(testSeriesId).
                <EntityNotFound>orElseThrow(() -> {
                    log.error("[subscribeTestSeries] testSeries not found userId [{}], id [{}]",userId,testSeriesId);
                    throw new EntityNotFound("TestSeries not found :Invalid TestSeriesID :" + testSeriesId);
                });

        purchasedTestSeriesRepository.findByUserIdAndAndTestSeriesId(userId, testSeriesId).
                <InvalidRequestException>orElseThrow(() -> {
            log.error("[subscribeTestSeries] testSeries already bought userId: [{}], id: [{}]",userId,testSeriesId);
            throw new InvalidRequestException("TestSeries already bought");
        });

        long id = counterService.increment(CounterEnum.PURCHASE_TEST_SERIES);
        Date date = new Date();
        PurchaseTestSeries purchaseTestSeries = PurchaseTestSeries.builder()
                .id(id)
                .userId(userId)
                .testSeriesId(testSeriesId)
                .createdDate(date)
                .createdDateLong(date.getTime())
                .build();
        purchasedTestSeriesRepository.save(purchaseTestSeries);

        userEntitlementService.createUserEntitlement(userId,testSeriesId, EntitlementType.TEST_SERIES);

        log.info("[subscribeTestSeries] subscribed successfully for userId: {}. testSeriesId: {}",userId, testSeriesId);
        return ApplicationConstant.SUBSCRIBED_MESSAGE;
    }


    public List<PaperInfo> getPaperInfoList(String userId,String testSeriesId, PaperCategory paperCategory, long after, long before){
        log.debug("[getPaperInfoList][TestSeriesServiceImpl] userId [{}], paperCategory [{}], before [{}]",userId,paperCategory,before);
        return testSeriesDao.getPaperInfoList(userId,testSeriesId,paperCategory,after,before);
    }

}
