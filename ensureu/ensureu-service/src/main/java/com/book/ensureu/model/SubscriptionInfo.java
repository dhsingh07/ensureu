package com.book.ensureu.model;

import com.book.ensureu.constant.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionInfo {

   // private String id; // TODO check if we need to store it or not
   // private long subscriptionId;
    private SubscriptionType subscriptionType;
    private String paperHierarchyId;//TODO this is equal to combination of papertype,tetstype,papercategory,papersubcategory
    private int paperCount; //TODO this can be used when we want limited paper only per month or smaller test series
}
