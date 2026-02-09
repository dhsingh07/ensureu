package com.book.ensureu.flow.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PercentileDto {

    private double marks;
    private double percentile;
    private String label;
    private int rank;
    private List<String> userIds;

}
