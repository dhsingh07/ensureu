package com.book.ensureu.admin.service.Impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.dto.FeatureConfigDto;
import com.book.ensureu.admin.model.FeatureConfigModel;
import com.book.ensureu.repository.FeatureConfigRepository;
import com.book.ensureu.admin.service.FeatureConfigService;

@Service
public class FeatureConfigServiceImpl implements FeatureConfigService {

    private static final String PLATFORM_FEATURES_ID = "PLATFORM_FEATURES";

    @Autowired
    private FeatureConfigRepository featureConfigRepository;

    @Override
    public FeatureConfigDto getFeatureConfig() {
        Optional<FeatureConfigModel> configOpt = featureConfigRepository.findById(PLATFORM_FEATURES_ID);
        if (configOpt.isPresent()) {
            return toDto(configOpt.get());
        }
        return getDefaultConfig();
    }

    @Override
    public FeatureConfigDto updateFeatureConfig(FeatureConfigDto featureConfigDto) {
        FeatureConfigModel model = FeatureConfigModel.builder()
                .id(PLATFORM_FEATURES_ID)
                .features(featureConfigDto.getFeatures())
                .updatedDate(System.currentTimeMillis())
                .updatedBy(featureConfigDto.getUpdatedBy())
                .build();
        FeatureConfigModel saved = featureConfigRepository.save(model);
        return toDto(saved);
    }

    private FeatureConfigDto toDto(FeatureConfigModel model) {
        return FeatureConfigDto.builder()
                .id(model.getId())
                .features(model.getFeatures())
                .updatedDate(model.getUpdatedDate())
                .updatedBy(model.getUpdatedBy())
                .build();
    }

    private FeatureConfigDto getDefaultConfig() {
        Map<String, Boolean> defaults = new LinkedHashMap<>();
        defaults.put("practiceMode", true);
        defaults.put("quizMode", true);
        defaults.put("mockTests", true);
        defaults.put("previousPapers", true);
        defaults.put("notifications", true);
        defaults.put("subscriptions", true);
        defaults.put("analytics", true);
        defaults.put("blogSection", true);
        return FeatureConfigDto.builder()
                .id(PLATFORM_FEATURES_ID)
                .features(defaults)
                .build();
    }
}
