package com.book.ensureu.admin.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureConfigDto {

    private String id;
    private Map<String, Boolean> features;
    private Long updatedDate;
    private String updatedBy;
}
