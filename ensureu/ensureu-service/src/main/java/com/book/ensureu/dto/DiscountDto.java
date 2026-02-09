package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@Data
public class DiscountDto {

    private String description;
    private List<PaperInfo> paperInfoList;
    private PaperSubCategory paperSubCategory;
    private PaperCategory paperCategory;
    private PaperType paperType;
    private String paperHierarchy;
    private Double discount;
    private Double discountPercentage;
    private ZonedDateTime validity;
}
