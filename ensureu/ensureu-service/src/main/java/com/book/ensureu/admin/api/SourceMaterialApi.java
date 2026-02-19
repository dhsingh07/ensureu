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
 * Admin API for managing source materials (PDFs, books) used for LLM processing.
 */
@Slf4j
@RestController
@RequestMapping("/admin/source-material")
@CrossOrigin
public class SourceMaterialApi {

    private static final List<String> ALLOWED_PDF_TYPES = Arrays.asList(
            "application/pdf"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    private static final long MAX_PDF_SIZE = 50 * 1024 * 1024; // 50 MB for PDFs

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload a PDF book/document for LLM processing
     * @param file PDF file
     * @param bookId Unique book/document ID
     * @param category Optional category for organization
     * @param title Optional title for the document
     * @return Response with file URL
     */
    @PostMapping("/pdf")
    public Response<Map<String, String>> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") String bookId,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam(value = "title", required = false) String title) {

        log.info("[SourceMaterialApi] Upload PDF - bookId: {}, category: {}", bookId, category);

        try {
            validatePdf(file);

            String folder = StorageFolderConstants.buildSourcePdfFolder(bookId);
            String originalFilename = file.getOriginalFilename();
            String filename = originalFilename != null ? originalFilename : "document.pdf";

            String fileUrl = fileStorageService.uploadFile(file, folder, filename);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("bookId", bookId);
            result.put("filename", filename);
            result.put("category", category);
            if (title != null) {
                result.put("title", title);
            }
            result.put("size", String.valueOf(file.getSize()));

            log.info("[SourceMaterialApi] PDF uploaded: {}", fileUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("PDF uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[SourceMaterialApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SourceMaterialApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload PDF: " + e.getMessage());
        }
    }

    /**
     * Upload reference material (text, doc, pdf)
     * @param file Document file
     * @param referenceId Unique reference ID
     * @param category Category for organization
     * @return Response with file URL
     */
    @PostMapping("/reference")
    public Response<Map<String, String>> uploadReference(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceId") String referenceId,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        log.info("[SourceMaterialApi] Upload reference - referenceId: {}, category: {}", referenceId, category);

        try {
            validateDocument(file);

            String folder = String.format("%s/%s/%s/%s",
                    StorageFolderConstants.SOURCE_MATERIALS,
                    StorageFolderConstants.REFERENCES,
                    category,
                    referenceId);

            String originalFilename = file.getOriginalFilename();
            String filename = originalFilename != null ? originalFilename : "reference.pdf";

            String fileUrl = fileStorageService.uploadFile(file, folder, filename);

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("referenceId", referenceId);
            result.put("filename", filename);
            result.put("category", category);
            result.put("contentType", file.getContentType());

            log.info("[SourceMaterialApi] Reference uploaded: {}", fileUrl);

            return new Response<Map<String, String>>()
                    .setStatus(200)
                    .setBody(result)
                    .setMessage("Reference material uploaded successfully");

        } catch (IllegalArgumentException e) {
            log.warn("[SourceMaterialApi] Validation error: {}", e.getMessage());
            return new Response<Map<String, String>>()
                    .setStatus(400)
                    .setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("[SourceMaterialApi] Upload failed: {}", e.getMessage(), e);
            return new Response<Map<String, String>>()
                    .setStatus(500)
                    .setMessage("Failed to upload reference: " + e.getMessage());
        }
    }

    /**
     * Delete source material
     * @param fileUrl URL of the file to delete
     * @return Response with deletion status
     */
    @DeleteMapping("/delete")
    public Response<Boolean> deleteSourceMaterial(@RequestParam("url") String fileUrl) {
        log.info("[SourceMaterialApi] Delete request: {}", fileUrl);

        try {
            boolean deleted = fileStorageService.deleteFile(fileUrl);

            if (deleted) {
                return new Response<Boolean>()
                        .setStatus(200)
                        .setBody(true)
                        .setMessage("Source material deleted successfully");
            } else {
                return new Response<Boolean>()
                        .setStatus(404)
                        .setBody(false)
                        .setMessage("Source material not found");
            }
        } catch (Exception e) {
            log.error("[SourceMaterialApi] Delete failed: {}", e.getMessage(), e);
            return new Response<Boolean>()
                    .setStatus(500)
                    .setBody(false)
                    .setMessage("Failed to delete source material: " + e.getMessage());
        }
    }

    /**
     * Get source material storage info
     * @return Storage configuration and folder structure
     */
    @GetMapping("/info")
    public Response<Map<String, Object>> getStorageInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("storageType", fileStorageService.getStorageType());
        info.put("maxPdfSize", MAX_PDF_SIZE);
        info.put("allowedPdfTypes", ALLOWED_PDF_TYPES);
        info.put("allowedDocumentTypes", ALLOWED_DOCUMENT_TYPES);

        // Folder structure
        Map<String, String> folders = new LinkedHashMap<>();
        folders.put("pdfs", "source-materials/pdfs/{bookId}/");
        folders.put("references", "source-materials/references/{category}/{referenceId}/");
        info.put("folderStructure", folders);

        return new Response<Map<String, Object>>()
                .setStatus(200)
                .setBody(info)
                .setMessage("Source material storage info retrieved");
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is empty");
        }

        if (file.getSize() > MAX_PDF_SIZE) {
            throw new IllegalArgumentException("PDF size exceeds maximum allowed size of 50MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_PDF_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF files are allowed");
        }
    }

    private void validateDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Document file is empty");
        }

        if (file.getSize() > MAX_PDF_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 50MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed: PDF, DOC, DOCX, TXT");
        }
    }
}
