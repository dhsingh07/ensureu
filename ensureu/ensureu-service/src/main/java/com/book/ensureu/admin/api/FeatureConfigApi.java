package com.book.ensureu.admin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.dto.FeatureConfigDto;
import com.book.ensureu.admin.service.FeatureConfigService;

@RestController
@RequestMapping("/admin/feature-config")
public class FeatureConfigApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureConfigApi.class);

    @Autowired
    private FeatureConfigService featureConfigService;

    @CrossOrigin
    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    public FeatureConfigDto getFeatureConfig() {
        LOGGER.info("getFeatureConfig");
        try {
            return featureConfigService.getFeatureConfig();
        } catch (Exception ex) {
            LOGGER.error("getFeatureConfig error", ex);
        }
        return null;
    }

    @CrossOrigin
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public FeatureConfigDto updateFeatureConfig(@RequestBody FeatureConfigDto featureConfigDto) {
        LOGGER.info("updateFeatureConfig");
        try {
            return featureConfigService.updateFeatureConfig(featureConfigDto);
        } catch (Exception ex) {
            LOGGER.error("updateFeatureConfig error", ex);
        }
        return null;
    }
}
