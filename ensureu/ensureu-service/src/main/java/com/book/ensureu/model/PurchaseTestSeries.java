package com.book.ensureu.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection ="purchasedTestSeries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PurchaseTestSeries {

    private long id;
    private String uuid;
    private String testSeriesId;
    private String userId;
    private long createdDateLong;
    private Date createdDate;


}
