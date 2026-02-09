package com.book.ensureu.dao;

import com.book.ensureu.constant.ApplicationConstant;
import com.book.ensureu.constant.EntitlementType;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperInfo;
import com.book.ensureu.model.PurchaseTestSeries;
import com.book.ensureu.common.model.TestSeries;
import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.repository.PurchasedTestSeriesRepository;
import com.book.ensureu.repository.TestSeriesRepository;
import com.book.ensureu.repository.UserEntitlementRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TestSeriesDao {


    private PurchasedTestSeriesRepository purchasedTestSeriesRepository;

    private TestSeriesRepository testSeriesRepository;

    private UserEntitlementRepository userEntitlementRepository;

    @Value("${testSeries.fetchMonth}")
    private int fetchMonth;

    /**
     @param paperCategory This will be used to filter paperInfoList
     @param testSeriesId Also used for filter and it's optional
     @param after fetch paperInfoList after this date by default it's currentDate - 2 months
     @param before fetch paperInfoList before this date by default it's currentDate
     @apiNote
     This method return paperInfoList for users who have bought test series using UserEntitlement collection
     which was using purchaseTestSeriesCollection
     **/
    public List<PaperInfo> getPaperInfoList(String userId, String testSeriesId , PaperCategory paperCategory, Long after, Long before){
        log.debug("[getPaperInfoList][TestSeriesDao] userId [{}], before [{}]",userId,before);
        Date date = new Date();
        long twoMonthMillis = fetchMonth* ApplicationConstant.MonthMillis;
        after = (Objects.nonNull(after) ? after : date.getTime()-twoMonthMillis);
        before = (Objects.nonNull(before)) ? before : date.getTime();
        List<PurchaseTestSeries> purchaseTestSeriesList;
        List<UserEntitlement> userEntitlementList;
        if(Objects.nonNull(testSeriesId)){
             userEntitlementList = userEntitlementRepository.findByUserIdAndActiveAndCreatedDateAndValidityAndEntitlementType(userId,true,after,before, EntitlementType.TEST_SERIES);
             //purchaseTestSeriesList = purchasedTestSeriesRepository.findByUserIdAndCreatedDateLongAfterAndCreatedDateLongBefore(userId,after,before);
        }else{
             userEntitlementList = userEntitlementRepository.findByUserIdAndEntitlementTypeAndActiveAndValidityBefore(userId,EntitlementType.TEST_SERIES,true,before);
             //purchaseTestSeriesList = purchasedTestSeriesRepository.findByUserIdAndCreatedDateLongAfterAndCreatedDateLongBefore(userId,after,before);
        }

        //List<String> testSeriesIdList = purchaseTestSeriesList.stream().map(PurchaseTestSeries::getTestSeriesId).collect(Collectors.toList());
        List<String> testSeriesIdList = userEntitlementList.stream().map(UserEntitlement::getTestSeriesId).collect(Collectors.toList());
        if (testSeriesIdList.isEmpty()){
            return Arrays.asList();
        }
        List<TestSeries> testSeriesList = testSeriesRepository.findByUuidIn(testSeriesIdList);
        log.debug("[getPaperInfoListFromTestSeries] testSeriesList {}",testSeriesList);
        Predicate<TestSeries> predicate = (Objects.nonNull(paperCategory)) ?
        testSeries -> testSeries.getPaperCategory().equals(paperCategory) : testSeries -> true;
        return testSeriesList.stream()
                .filter(predicate)
                .map(testSeries ->
                     testSeries.getPaperSubCategoryInfoList()
                        .stream()
                        .flatMap(paperSubCategoryInfo -> paperSubCategoryInfo.getPaperInfoList()
                                .stream()).collect(Collectors.toList()))
                .flatMap(paperInfos -> paperInfos.stream())
                .collect(Collectors.toList());

    }

}
