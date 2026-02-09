package com.book.ensureu.common.transformer;

import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.common.model.TestSeries;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperType;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestSeriesTransformer {

    public static TestSeriesDto toDTO(TestSeries series){
        return TestSeriesDto.builder()
                .uuid(series.getUuid())
                .description(series.getDescription())
                .price(series.getActualPrice())
                .discountedPrice(series.getDiscountedPrice())
                .discountedPercentage(series.getDiscountPercentage())
                .validity(series.getValidity())
                .paperSubCategoryInfoList(addPaperSubCategoryInfoData(series))
                .build();
    }

    public static List<TestSeriesDto> toDTOs(List<TestSeries> testSeriesList){
       List<TestSeriesDto> testSeriesDtoList = new LinkedList<>();
        if(testSeriesList !=null && !testSeriesList.isEmpty()){
          testSeriesList.forEach(o->{
              testSeriesDtoList.add(toDTO(o));
          });
        }
        return testSeriesDtoList;
    }

    public static List<TestSeriesDto.paperSubCategoryInfoDto> addPaperSubCategoryInfoData(TestSeries series){

        List<TestSeriesDto.paperSubCategoryInfoDto> paperSubCategoryInfoDtoList = new LinkedList<>();
        series.getPaperSubCategoryInfoList().forEach(paperSubCategoryInfo -> {
            paperSubCategoryInfoDtoList.add(TestSeriesDto.paperSubCategoryInfoDto.builder()
                    .paperCount(paperSubCategoryInfo.getPaperCount())
                    .paperSubCategory(paperSubCategoryInfo.getPaperSubCategory())
                    .build());
        });
        return paperSubCategoryInfoDtoList;
    }

    public static List<TestSeries.paperSubCategoryInfo> getPaperSubCategoryInfo
            (List<TestSeriesDto.paperSubCategoryInfoDto> paperSubCategoryInfoDtos){
        List<TestSeries.paperSubCategoryInfo> paperSubCategoryInfos = new LinkedList<>();
        paperSubCategoryInfoDtos.forEach(obj -> {
            paperSubCategoryInfos.add(
                    TestSeries.paperSubCategoryInfo.builder()
                            .paperCount(obj.getPaperCount())
                            .paperSubCategory(obj.getPaperSubCategory())
                            .build()
            );
        });
        return  paperSubCategoryInfos;
    }


    public static TestSeries dtoToModel(TestSeriesDto testSeriesDto){
        return TestSeries.builder()
                .uuid(testSeriesDto.getUuid())
                .description(testSeriesDto.getDescription())
                .validityDate(new Date(testSeriesDto.getValidity()))
                .validity(testSeriesDto.getValidity())
                .active(testSeriesDto.isActive())
                .actualPrice(testSeriesDto.getPrice())
                .discountedPrice(testSeriesDto.getDiscountedPrice())
                .discountPercentage(testSeriesDto.getDiscountedPercentage())
                .paperCategory(PaperCategory.valueOf(testSeriesDto.getPaperCategory()))
                .paperType(PaperType.valueOf(testSeriesDto.getPaperType()))
                .paperSubCategoryInfoList(getPaperSubCategoryInfo(testSeriesDto.getPaperSubCategoryInfoList()))
                .build();
    }

}
