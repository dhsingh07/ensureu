package com.book.ensureu.admin.service.Impl;

import com.book.ensureu.admin.dao.PriceMetaDataDao;
import com.book.ensureu.admin.service.PriceMetaDataService;
import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PriceMetaDataServiceImpl implements PriceMetaDataService {

    private PriceMetaDataDao priceMetaDataDao;

    @Override
    public List<PriceMetaDataDto> getPriceMetaData(PaperSubCategory paperSubCategory) {
        log.debug("[getPriceMetaData] paperSubCategory {}",paperSubCategory);
        return priceMetaDataDao.getPriceMetaData(paperSubCategory);
    }

    @Override
    public void savePriceMetaData(PriceMetaDataDto priceMetaDataDto) {
        log.debug("[savePriceMetaData] priceMetaDataDto {}",priceMetaDataDto);
        priceMetaDataDao.savePriceMetaData(priceMetaDataDto);
    }

    //TODO need to add later
    public PriceMetaDataDto getPriceMetaData(PaperSubCategory paperSubCategory, SubscriptionType subscriptionType) {
        return null;
    }

    @Override
    public void patchPriceMetaData(PriceMetaDataDto priceMetaDataDto) {
        log.debug("[patchPriceMetaData] priceMetaDataDto {}",priceMetaDataDto);
        priceMetaDataDao.patchPriceMetaData(priceMetaDataDto);
    }


}
