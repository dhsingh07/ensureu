package com.book.ensureu.common.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestSeriesDto {

    private String uuid;
    private String description;
    private double price;
    private double discountedPrice;
    private double discountedPercentage;
    private long validity;
    private boolean active;
    private String paperCategory;
    private String paperType;

    private List<paperSubCategoryInfoDto> paperSubCategoryInfoList;

    @Data
    @Builder
    public static class paperSubCategoryInfoDto {
        PaperSubCategory paperSubCategory;
        int paperCount;
    }
}
