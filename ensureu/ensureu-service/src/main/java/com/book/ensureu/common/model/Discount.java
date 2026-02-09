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
public class Discount {

    private String id;
    private String description;
    private List<PaperInfo> paperInfoList;
    private PaperSubCategory paperSubCategory;
    private PaperCategory paperCategory;
    private PaperType paperType;
    private String paperHierarchy;
    private List<String> userIds; // in-case user specific offers
    private Double discount;
    private Double discountPercentage;
    private String createdDate;
    private Date validity;





}
