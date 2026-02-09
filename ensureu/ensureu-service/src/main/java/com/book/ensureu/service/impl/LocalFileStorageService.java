package com.book.ensureu.service.impl;

import com.book.ensureu.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local file system storage implementation.
 * Stores files on the local server file system.
 */
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    @Value("${storage.local.base-path:/var/ensureu/uploads}")
    private String basePath;

    @Value("${storage.local.base-url:http://localhost:8282/api/files}")
    private String baseUrl;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;
        return uploadFile(file, folder, filename);
    }

    @Override
    public String uploadFile(MultipartFile file, String folder, String filename) {
        try {
            // Ensure filename has extension
            String originalFilename = file.getOriginalFilename();
            if (!filename.contains(".") && originalFilename != null) {
                filename = filename + getFileExtension(originalFilename);
            }

            Path folderPath = Paths.get(basePath, folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileKey = folder + "/" + filename;
            log.info("[LocalStorage] File uploaded: {}", fileKey);

            return getPublicUrl(fileKey);
        } catch (IOException e) {
            log.error("[LocalStorage] Failed to upload file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String folder, String filename, String contentType, long contentLength) {
        try {
            Path folderPath = Paths.get(basePath, folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileKey = folder + "/" + filename;
            log.info("[LocalStorage] File uploaded from stream: {}", fileKey);

            return getPublicUrl(fileKey);
        } catch (IOException e) {
            log.error("[LocalStorage] Failed to upload file from stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String fileKey = extractFileKey(fileUrl);
            Path filePath = Paths.get(basePath, fileKey);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("[LocalStorage] File deleted: {}", fileKey);
                return true;
            } else {
                log.warn("[LocalStorage] File not found for deletion: {}", fileKey);
                return false;
            }
        } catch (IOException e) {
            log.error("[LocalStorage] Failed to delete file: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public InputStream getFile(String fileUrl) {
        try {
            String fileKey = extractFileKey(fileUrl);
            Path filePath = Paths.get(basePath, fileKey);

            if (Files.exists(filePath)) {
                return new FileInputStream(filePath.toFile());
            } else {
                log.warn("[LocalStorage] File not found: {}", fileKey);
                return null;
            }
        } catch (IOException e) {
            log.error("[LocalStorage] Failed to get file: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        String fileKey = extractFileKey(fileUrl);
        Path filePath = Paths.get(basePath, fileKey);
        return Files.exists(filePath);
    }

    @Override
    public String getPublicUrl(String fileKey) {
        return baseUrl + "/" + fileKey;
    }

    @Override
    public String getStorageType() {
        return "LOCAL";
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String extractFileKey(String fileUrl) {
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length() + 1);
        }
        // If it's already a file key (not a URL)
        return fileUrl;
    }
}
