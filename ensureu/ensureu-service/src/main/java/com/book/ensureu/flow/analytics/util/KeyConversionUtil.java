package com.book.ensureu.flow.analytics.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyConversionUtil {

    public String getDoubleToKey(Double key) {
        if (key == null) {
            key = 0.0;
        }
        String keyStr = String.valueOf(key);
        return keyStr.replace(".", "_");
    }

    public Double getDoubleFromKey(String key) {
        if (key == null) {
            return 0.0;
        }
        key = key.replace("_", ".");
        return Double.valueOf(key);
    }

    public double safeDoubleValue(Double value) {
        return value != null ? value : 0.0;
    }
}
