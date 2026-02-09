package com.book.ensureu.api;

import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for file storage operations.
 * Supports uploading, downloading, and deleting files.
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileStorageApi {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload a file
     * @param file The file to upload
     * @param folder The folder/category (e.g., "questions", "papers", "blogs")
     * @return Response with file URL
     */
    @CrossOrigin
    @PostMapping("/upload")
    public Response<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        log.info("[FileStorageApi] Upload request - folder: {}, filename: {}, size: {}",
                folder, file.getOriginalFilename(), file.getSize());

        try {
            // Validate file
            validateFile(file);

            // Upload file
            String fileUrl = fileStorageService.uploadFile(file, folder);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", file.getOriginalFilename());
            result.put("contentType", file.getContentType());
            result.put("size", String.valueOf(file.getSize()));
            result.put("storageType", fileStorageService.getStorageType());

            log.info("[FileStorageApi] File uploaded successfully: {}", fileUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("File uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[FileStorageApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());

        } catch (Exception e) {
            log.error("[FileStorageApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Upload an image specifically for questions
     * @param file The image file
     * @return Response with image URL
     */
    @CrossOrigin
    @PostMapping("/upload/image")
    public Response<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "questions") String folder) {

        log.info("[FileStorageApi] Image upload request - folder: {}", folder);

        try {
            // Validate image
            validateImage(file);

            // Upload file
            String fileUrl = fileStorageService.uploadFile(file, folder);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", file.getOriginalFilename());

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Image uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[FileStorageApi] Image validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());

        } catch (Exception e) {
            log.error("[FileStorageApi] Image upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Delete a file
     * @param fileUrl The URL of the file to delete
     * @return Response with deletion status
     */
    @CrossOrigin
    @DeleteMapping("/delete")
    public Response<Boolean> deleteFile(@RequestParam("url") String fileUrl) {
        log.info("[FileStorageApi] Delete request - url: {}", fileUrl);

        try {
            boolean deleted = fileStorageService.deleteFile(fileUrl);

            if (deleted) {
                return new Response<Boolean>()
                        .setStatus(200)
                        .setBody(true)
                        .setMessage("File deleted successfully");
            } else {
                return new Response<Boolean>()
                        .setStatus(404)
                        .setBody(false)
                        .setMessage("File not found");
            }

        } catch (Exception e) {
            log.error("[FileStorageApi] Delete failed: {}", e.getMessage(), e);
            return new Response<Boolean>()
                    .setStatus(500)
                    .setBody(false)
                    .setMessage("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Serve a file (for local storage)
     * @param folder The folder
     * @param filename The filename
     * @return The file content
     */
    @CrossOrigin
    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<InputStreamResource> getFile(
            @PathVariable String folder,
            @PathVariable String filename) {

        log.info("[FileStorageApi] Get file request - folder: {}, filename: {}", folder, filename);

        try {
            String fileKey = folder + "/" + filename;
            InputStream inputStream = fileStorageService.getFile(fileKey);

            if (inputStream == null) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type from filename
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            log.error("[FileStorageApi] Get file failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get storage info
     * @return Storage type and configuration info
     */
    @CrossOrigin
    @GetMapping("/info")
    public Response<Map<String, String>> getStorageInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("storageType", fileStorageService.getStorageType());
        info.put("maxFileSize", String.valueOf(MAX_FILE_SIZE));

        return new Response<Map<String, String>>()
                .setStatus(200)
                .setBody(info)
                .setMessage("Storage info retrieved");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Could not determine file type");
        }

        // Allow images and documents
        if (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image size exceeds maximum allowed size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid image type. Allowed: JPEG, PNG, GIF, WebP, SVG");
        }
    }

    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFilename.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFilename.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerFilename.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }
}
