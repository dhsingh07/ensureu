package com.book.ensureu.flow.analytics.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPaperTimeSeriesDto {

    private String id;

    private String userId;

    private String paperId;

    private List<UserQuestionTimeDto> userQuestionTimeDtoList;
}
