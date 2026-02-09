package com.book.ensureu.configuration;

import com.book.ensureu.service.FileStorageService;
import com.book.ensureu.service.impl.LocalFileStorageService;
import com.book.ensureu.service.impl.S3FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for file storage.
 * Supports switching between local file system and AWS S3.
 */
@Slf4j
@Configuration
public class FileStorageConfig {

    @Value("${storage.type:LOCAL}")
    private String storageType;

    @Bean
    public FileStorageService fileStorageService() {
        if ("S3".equalsIgnoreCase(storageType)) {
            log.info("[FileStorage] Using S3 storage");
            return new S3FileStorageService();
        } else {
            log.info("[FileStorage] Using local storage");
            return new LocalFileStorageService();
        }
    }
}
