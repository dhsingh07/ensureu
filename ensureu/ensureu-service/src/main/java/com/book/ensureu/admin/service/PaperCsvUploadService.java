package com.book.ensureu.admin.service;

import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for processing CSV uploads and creating papers in the database.
 */
public interface PaperCsvUploadService {

    /**
     * Process a CSV file: upload to S3 first, then create a paper in the appropriate collection.
     *
     * @param file The CSV file
     * @return Map containing paper details (paperId, paperName, totalQuestions, sections, csvUrl)
     * @throws Exception if processing fails
     */
    Map<String, Object> processCsvAndCreatePaper(MultipartFile file) throws Exception;

    /**
     * Process a CSV file from input stream (legacy method, doesn't upload to S3)
     *
     * @param inputStream The CSV file input stream
     * @return Map containing paper details (paperId, paperName, totalQuestions, sections)
     * @throws Exception if processing fails
     */
    Map<String, Object> processCsvAndCreatePaper(InputStream inputStream) throws Exception;
}
