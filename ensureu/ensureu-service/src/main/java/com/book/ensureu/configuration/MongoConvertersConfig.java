package com.book.ensureu.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.book.ensureu.constant.RoleType;

@Configuration
public class MongoConvertersConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new StringToRoleTypeConverter()
        ));
    }

    @ReadingConverter
    public static class StringToRoleTypeConverter implements Converter<String, RoleType> {
        @Override
        public RoleType convert(String source) {
            return RoleType.fromString(source);
        }
    }
}
