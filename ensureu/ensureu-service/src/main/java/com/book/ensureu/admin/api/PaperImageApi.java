package com.book.ensureu.admin.api;

import com.book.ensureu.constant.StorageFolderConstants;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * EnsureU - Assessment Platform for Competitive Exams
 * A product of GrayscaleLabs AI Pvt Ltd.
 *
 * Admin API for managing paper and question images.
 * Handles uploads to S3/Local storage with proper folder structure.
 */
@Slf4j
@RestController
@RequestMapping("/admin/paper-image")
@CrossOrigin
public class PaperImageApi {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB per image

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload paper cover image
     * @param file Cover image file
     * @param category Paper category (SSC_CGL, BANK_PO, etc.)
     * @param paperId Paper ID
     * @param isPaid Whether paper is paid
     * @return Response with image URL
     */
    @PostMapping("/cover")
    public Response<Map<String, String>> uploadPaperCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam("paperId") String paperId,
            @RequestParam(value = "isPaid", defaultValue = "false") boolean isPaid) {

        log.info("[PaperImageApi] Upload cover - category: {}, paperId: {}, isPaid: {}",
                category, paperId, isPaid);

        try {
            validateImage(file);

            String folder = StorageFolderConstants.buildPaperFolder(category, isPaid, paperId);
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = "cover" + extension;

            String imageUrl = fileStorageService.uploadFile(file, folder, filename);

            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("type", "cover");
            result.put("paperId", paperId);

            log.info("[PaperImageApi] Cover uploaded: {}", imageUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Paper cover uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[PaperImageApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[PaperImageApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload cover: " + e.getMessage());
        }
    }

    /**
     * Upload question image (problem, option, or solution)
     * @param file Image file
     * @param category Paper category
     * @param paperId Paper ID
     * @param isPaid Whether paper is paid
     * @param questionNumber Question number
     * @param imageType Type of image (problem, problem-hindi, option-a, option-b, option-c, option-d, solution)
     * @return Response with image URL
     */
    @PostMapping("/question")
    public Response<Map<String, String>> uploadQuestionImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam("paperId") String paperId,
            @RequestParam(value = "isPaid", defaultValue = "false") boolean isPaid,
            @RequestParam("questionNumber") int questionNumber,
            @RequestParam("imageType") String imageType) {

        log.info("[PaperImageApi] Upload question image - category: {}, paperId: {}, q: {}, type: {}",
                category, paperId, questionNumber, imageType);

        try {
            validateImage(file);
            validateImageType(imageType);

            String folder = StorageFolderConstants.buildPaperQuestionsFolder(category, isPaid, paperId);
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = StorageFolderConstants.buildQuestionImageFilename(
                    questionNumber, imageType, extension.substring(1));

            String imageUrl = fileStorageService.uploadFile(file, folder, filename);

            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("type", imageType);
            result.put("questionNumber", String.valueOf(questionNumber));
            result.put("paperId", paperId);

            log.info("[PaperImageApi] Question image uploaded: {}", imageUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Question image uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[PaperImageApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[PaperImageApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload question image: " + e.getMessage());
        }
    }

    /**
     * Batch upload multiple question images
     * @param files Image files (max 10)
     * @param category Paper category
     * @param paperId Paper ID
     * @param isPaid Whether paper is paid
     * @param questionNumber Question number
     * @param imageTypes Comma-separated list of image types corresponding to files
     * @return Response with list of uploaded URLs
     */
    @PostMapping("/question/batch")
    public Response<List<Map<String, String>>> uploadQuestionImagesBatch(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("category") String category,
            @RequestParam("paperId") String paperId,
            @RequestParam(value = "isPaid", defaultValue = "false") boolean isPaid,
            @RequestParam("questionNumber") int questionNumber,
            @RequestParam("imageTypes") String imageTypes) {

        log.info("[PaperImageApi] Batch upload - category: {}, paperId: {}, q: {}, count: {}",
                category, paperId, questionNumber, files.length);

        try {
            if (files.length > 10) {
                throw new IllegalArgumentException("Maximum 10 files allowed per batch");
            }

            String[] types = imageTypes.split(",");
            if (types.length != files.length) {
                throw new IllegalArgumentException("Number of image types must match number of files");
            }

            List<Map<String, String>> results = new ArrayList<>();
            String folder = StorageFolderConstants.buildPaperQuestionsFolder(category, isPaid, paperId);

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String imageType = types[i].trim();

                validateImage(file);
                validateImageType(imageType);

                String extension = getFileExtension(file.getOriginalFilename());
                String filename = StorageFolderConstants.buildQuestionImageFilename(
                        questionNumber, imageType, extension.substring(1));

                String imageUrl = fileStorageService.uploadFile(file, folder, filename);

                Map<String, String> result = new HashMap<>();
                result.put("url", imageUrl);
                result.put("type", imageType);
                result.put("filename", filename);
                results.add(result);
            }

            log.info("[PaperImageApi] Batch upload complete: {} images", results.size());

            return new Response<List<Map<String, String>>>()
                    .setStatus(200)
                    .setBody(results)
                    .setMessage("Batch upload successful");

        } catch (IllegalArgumentException e) {
            log.warn("[PaperImageApi] Batch validation error: {}", e.getMessage());
            return new Response<List<Map<String, String>>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[PaperImageApi] Batch upload failed: {}", e.getMessage(), e);
            return new Response<List<Map<String, String>>>()
                    .setStatus(500)
                    .setMessage("Batch upload failed: " + e.getMessage());
        }
    }

    /**
     * Upload image to question bank (standalone questions not tied to a specific paper)
     * @param file Image file
     * @param category Paper category
     * @param questionId Question ID
     * @param imageType Type of image
     * @return Response with image URL
     */
    @PostMapping("/question-bank")
    public Response<Map<String, String>> uploadQuestionBankImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam("questionId") String questionId,
            @RequestParam("imageType") String imageType) {

        log.info("[PaperImageApi] Upload to question bank - category: {}, questionId: {}, type: {}",
                category, questionId, imageType);

        try {
            validateImage(file);
            validateImageType(imageType);

            String folder = StorageFolderConstants.buildQuestionBankFolder(category, questionId);
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = imageType + extension;

            String imageUrl = fileStorageService.uploadFile(file, folder, filename);

            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("type", imageType);
            result.put("questionId", questionId);

            log.info("[PaperImageApi] Question bank image uploaded: {}", imageUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Question bank image uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[PaperImageApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[PaperImageApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload question bank image: " + e.getMessage());
        }
    }

    /**
     * Delete paper image
     * @param imageUrl URL of the image to delete
     * @return Response with deletion status
     */
    @DeleteMapping("/delete")
    public Response<Boolean> deleteImage(@RequestParam("url") String imageUrl) {
        log.info("[PaperImageApi] Delete request: {}", imageUrl);

        try {
            boolean deleted = fileStorageService.deleteFile(imageUrl);

            if (deleted) {
                return new Response<Boolean>()
                        .setStatus(200)
                        .setBody(true)
                        .setMessage("Image deleted successfully");
            } else {
                return new Response<Boolean>()
                        .setStatus(404)
                        .setBody(false)
                        .setMessage("Image not found");
            }
        } catch (Exception e) {
            log.error("[PaperImageApi] Delete failed: {}", e.getMessage(), e);
            return new Response<Boolean>()
                    .setStatus(500)
                    .setBody(false)
                    .setMessage("Failed to delete image: " + e.getMessage());
        }
    }

    /**
     * Get storage info and folder structure
     * @return Storage configuration details
     */
    @GetMapping("/info")
    public Response<Map<String, Object>> getStorageInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("storageType", fileStorageService.getStorageType());
        info.put("maxFileSize", MAX_FILE_SIZE);
        info.put("allowedTypes", ALLOWED_IMAGE_TYPES);

        // Folder structure documentation
        Map<String, String> folders = new LinkedHashMap<>();
        folders.put("papers", "papers/{category}/{free|paid}/{paperId}/");
        folders.put("questions", "papers/{category}/{free|paid}/{paperId}/questions/");
        folders.put("questionBank", "question-bank/{category}/{questionId}/");
        folders.put("sourcesPdf", "source-materials/pdfs/{bookId}/");
        folders.put("aiGenerated", "generated-content/ai-questions/{batchId}/");
        info.put("folderStructure", folders);

        // Image types
        List<String> imageTypes = Arrays.asList(
                StorageFolderConstants.PROBLEM_IMAGE,
                StorageFolderConstants.PROBLEM_HINDI_IMAGE,
                StorageFolderConstants.OPTION_A,
                StorageFolderConstants.OPTION_B,
                StorageFolderConstants.OPTION_C,
                StorageFolderConstants.OPTION_D,
                StorageFolderConstants.SOLUTION_IMAGE
        );
        info.put("imageTypes", imageTypes);

        return new Response<Map<String, Object>>()
                .setStatus(200)
                .setBody(info)
                .setMessage("Storage info retrieved");
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Image size exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid image type. Allowed: JPEG, PNG, GIF, WebP, SVG");
        }
    }

    private void validateImageType(String imageType) {
        List<String> validTypes = Arrays.asList(
                StorageFolderConstants.PROBLEM_IMAGE,
                StorageFolderConstants.PROBLEM_HINDI_IMAGE,
                StorageFolderConstants.OPTION_A,
                StorageFolderConstants.OPTION_B,
                StorageFolderConstants.OPTION_C,
                StorageFolderConstants.OPTION_D,
                StorageFolderConstants.SOLUTION_IMAGE
        );

        if (!validTypes.contains(imageType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid image type. Must be one of: " + String.join(", ", validTypes));
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Default extension
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
