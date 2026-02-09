package com.book.ensureu.common.transformer;

import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.common.model.PriceMetaData;
import org.springframework.stereotype.Component;


@Component
public class PriceMetaDataTransformer implements Transformer<PriceMetaData, PriceMetaDataDto> {

    @Override
    public PriceMetaData dtoToModel(PriceMetaDataDto priceMetaDataDto) {

        return PriceMetaData.builder()
                .id(priceMetaDataDto.getId())
                .discountedPrice(priceMetaDataDto.getDiscountedPrice())
                .discountedPricePerPaper(priceMetaDataDto.getDiscountedPricePerPaper())
                .discountPercentage(priceMetaDataDto.getDiscountPercentage())
                .extraPaperCount(priceMetaDataDto.getExtraPaperCount())
                .minPaperCount(priceMetaDataDto.getMinPaperCount())
                .numberOfPapers(priceMetaDataDto.getNumberOfPapers())
                .paperCategory(priceMetaDataDto.getPaperCategory())
                .paperSubCategory(priceMetaDataDto.getPaperSubCategory())
                .paperType(priceMetaDataDto.getPaperType())
                .price(priceMetaDataDto.getPrice())
                .pricePerPaper(priceMetaDataDto.getPricePerPaper())
                .subscriptionType(priceMetaDataDto.getSubscriptionType())
                .build();
    }

    @Override
    public PriceMetaDataDto modelToDto(PriceMetaData priceMetaData) {
        return PriceMetaDataDto.builder()
                .id(priceMetaData.getId())
                .discountedPrice(priceMetaData.getDiscountedPrice())
                .discountedPricePerPaper(priceMetaData.getDiscountedPricePerPaper())
                .discountPercentage(priceMetaData.getDiscountPercentage())
                .extraPaperCount(priceMetaData.getExtraPaperCount())
                .minPaperCount(priceMetaData.getMinPaperCount())
                .numberOfPapers(priceMetaData.getNumberOfPapers())
                .paperCategory(priceMetaData.getPaperCategory())
                .paperSubCategory(priceMetaData.getPaperSubCategory())
                .paperType(priceMetaData.getPaperType())
                .price(priceMetaData.getPrice())
                .pricePerPaper(priceMetaData.getPricePerPaper())
                .subscriptionType(priceMetaData.getSubscriptionType())
                .build();
    }
}
