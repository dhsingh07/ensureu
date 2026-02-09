package com.book.ensureu.service.impl;

import com.book.ensureu.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * AWS S3 storage implementation.
 * Stores files in AWS S3 bucket.
 */
@Slf4j
public class S3FileStorageService implements FileStorageService {

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    @Value("${storage.s3.region:ap-south-1}")
    private String region;

    @Value("${storage.s3.access-key}")
    private String accessKey;

    @Value("${storage.s3.secret-key}")
    private String secretKey;

    @Value("${storage.s3.base-url:}")
    private String customBaseUrl;

    @Value("${storage.s3.prefix:uploads}")
    private String keyPrefix;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
            log.info("[S3Storage] Initialized S3 client for bucket: {} in region: {}", bucketName, region);
        } catch (Exception e) {
            log.error("[S3Storage] Failed to initialize S3 client: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize S3 client", e);
        }
    }

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

            String key = buildKey(folder, filename);
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("[S3Storage] File uploaded: s3://{}/{}", bucketName, key);
            return getPublicUrl(key);
        } catch (IOException e) {
            log.error("[S3Storage] Failed to upload file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String folder, String filename, String contentType, long contentLength) {
        try {
            String key = buildKey(folder, filename);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, contentLength));

            log.info("[S3Storage] File uploaded from stream: s3://{}/{}", bucketName, key);
            return getPublicUrl(key);
        } catch (Exception e) {
            log.error("[S3Storage] Failed to upload file from stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("[S3Storage] File deleted: s3://{}/{}", bucketName, key);
            return true;
        } catch (Exception e) {
            log.error("[S3Storage] Failed to delete file: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public InputStream getFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObject(getRequest);
        } catch (NoSuchKeyException e) {
            log.warn("[S3Storage] File not found: {}", fileUrl);
            return null;
        } catch (Exception e) {
            log.error("[S3Storage] Failed to get file: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("[S3Storage] Failed to check file existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getPublicUrl(String fileKey) {
        if (customBaseUrl != null && !customBaseUrl.isEmpty()) {
            // Use custom CDN/CloudFront URL
            return customBaseUrl + "/" + fileKey;
        }
        // Default S3 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileKey);
    }

    @Override
    public String getStorageType() {
        return "S3";
    }

    private String buildKey(String folder, String filename) {
        if (keyPrefix != null && !keyPrefix.isEmpty()) {
            return keyPrefix + "/" + folder + "/" + filename;
        }
        return folder + "/" + filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String extractKeyFromUrl(String fileUrl) {
        // Handle custom base URL
        if (customBaseUrl != null && !customBaseUrl.isEmpty() && fileUrl.startsWith(customBaseUrl)) {
            return fileUrl.substring(customBaseUrl.length() + 1);
        }

        // Handle standard S3 URL
        String s3UrlPrefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
        if (fileUrl.startsWith(s3UrlPrefix)) {
            return fileUrl.substring(s3UrlPrefix.length());
        }

        // Handle s3:// URLs
        if (fileUrl.startsWith("s3://")) {
            String withoutProtocol = fileUrl.substring(5);
            int slashIndex = withoutProtocol.indexOf('/');
            if (slashIndex > 0) {
                return withoutProtocol.substring(slashIndex + 1);
            }
        }

        // Assume it's already a key
        return fileUrl;
    }
}
