package com.book.ensureu.admin.service;

import com.book.ensureu.admin.dto.FeatureConfigDto;

public interface FeatureConfigService {

    FeatureConfigDto getFeatureConfig();

    FeatureConfigDto updateFeatureConfig(FeatureConfigDto featureConfigDto);
}
