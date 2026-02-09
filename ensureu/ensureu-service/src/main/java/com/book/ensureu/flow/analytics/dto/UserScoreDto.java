package com.book.ensureu.flow.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserScoreDto {

    private String userId;

    private String name;

    private String paperDescription;

    private Double score;

    private Double maxPossibleScore;

    private long totalSubmissionsPerCategory;
//
//    private int totalPaperSubmitted;
//
//    private Double averageScore;

    private Integer rank;

}
