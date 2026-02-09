package com.book.ensureu.common.model;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.PaperInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@Builder
@Data
public class TestSeries extends AuditData{

    private long id;
    private String uuid;
    private String name;
    private String description;
    private List<paperSubCategoryInfo> paperSubCategoryInfoList;
    private PaperType paperType;
    private String paperHierarchy;

    /* TestSeries will be based on category only
    *
    */
    private PaperCategory paperCategory;
    private double discountPercentage;
    private double discountedPrice;
    private double actualPrice;
    private boolean active;
    private long validity;
    private Date validityDate;
    private Date crDate;



    @Data
    @Builder
    public static class paperSubCategoryInfo{
        PaperSubCategory paperSubCategory;
        int paperCount;
        List<PaperInfo> paperInfoList;
    }

}
