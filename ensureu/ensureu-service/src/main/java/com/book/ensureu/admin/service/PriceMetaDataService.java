package com.book.ensureu.admin.service;

import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.SubscriptionType;

import java.util.List;

public interface PriceMetaDataService {

    List<PriceMetaDataDto> getPriceMetaData(PaperSubCategory paperSubCategory);

    void savePriceMetaData(PriceMetaDataDto priceMetaDataDto);

    PriceMetaDataDto getPriceMetaData(PaperSubCategory paperSubCategory, SubscriptionType subscriptionType);

    void patchPriceMetaData(PriceMetaDataDto priceMetaDataDto);
}
