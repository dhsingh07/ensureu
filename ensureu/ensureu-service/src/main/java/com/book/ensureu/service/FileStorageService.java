package com.book.ensureu.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Interface for file storage operations.
 * Supports both local file system and cloud storage (S3).
 */
public interface FileStorageService {

    /**
     * Upload a file
     * @param file The file to upload
     * @param folder The folder/prefix to store the file in (e.g., "questions", "papers", "blogs")
     * @return The URL or path to access the uploaded file
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Upload a file with a specific filename
     * @param file The file to upload
     * @param folder The folder/prefix
     * @param filename The desired filename (without extension)
     * @return The URL or path to access the uploaded file
     */
    String uploadFile(MultipartFile file, String folder, String filename);

    /**
     * Upload file from input stream
     * @param inputStream The input stream
     * @param folder The folder/prefix
     * @param filename The filename with extension
     * @param contentType The MIME type
     * @param contentLength The content length
     * @return The URL or path to access the uploaded file
     */
    String uploadFile(InputStream inputStream, String folder, String filename, String contentType, long contentLength);

    /**
     * Delete a file
     * @param fileUrl The URL or path of the file to delete
     * @return true if deleted successfully
     */
    boolean deleteFile(String fileUrl);

    /**
     * Get a file as input stream
     * @param fileUrl The URL or path of the file
     * @return InputStream of the file content
     */
    InputStream getFile(String fileUrl);

    /**
     * Check if a file exists
     * @param fileUrl The URL or path of the file
     * @return true if the file exists
     */
    boolean fileExists(String fileUrl);

    /**
     * Get the public URL for a file
     * @param fileKey The file key/path
     * @return The public URL
     */
    String getPublicUrl(String fileKey);

    /**
     * Get the storage type (LOCAL or S3)
     * @return The storage type
     */
    String getStorageType();
}
