package com.book.ensureu.admin.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feature_config")
public class FeatureConfigModel {

    @Id
    private String id;

    private Map<String, Boolean> features;

    private Long updatedDate;

    private String updatedBy;
}
