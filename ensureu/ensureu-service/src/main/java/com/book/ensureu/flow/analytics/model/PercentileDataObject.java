package com.book.ensureu.flow.analytics.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PercentileDataObject {

    private double marks;
    private double percentile;
    //Symbol i.e %
    private String label;
    private int rank;
    private List<String> userIds;

}
