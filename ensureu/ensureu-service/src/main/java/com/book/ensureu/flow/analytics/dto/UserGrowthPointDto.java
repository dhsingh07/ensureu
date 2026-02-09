package com.book.ensureu.flow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGrowthPointDto {

    private double userMarks;
    private double topperMarks;
    private double avgMarks;
    private String paperName;
    private String paperId;
    private double totalMarks; //TODO need to add
    private double userPercentile;
    private double avgPercentile;


}
