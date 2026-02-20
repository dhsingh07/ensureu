package com.book.ensureu.admin.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.admin.service.PaperCsvUploadService;
import com.book.ensureu.response.dto.Response;

/**
 * API for uploading SSC papers via CSV file.
 *
 * <p>Endpoint: POST /admin/paper/upload/csv</p>
 *
 * <p>The CSV file should follow the format defined in SSC_PAPER_CSV_FORMAT.md</p>
 */
@RestController
@RequestMapping("/admin/paper/upload")
@CrossOrigin
public class PaperCsvUploadApi {

    private static final Logger logger = LoggerFactory.getLogger(PaperCsvUploadApi.class);

    @Autowired
    private PaperCsvUploadService paperCsvUploadService;

    /**
     * Upload a CSV file containing SSC paper questions.
     *
     * <p>The CSV should contain:</p>
     * <ul>
     *   <li>Paper metadata (name, category, test type, time, marks)</li>
     *   <li>Section information (name, type)</li>
     *   <li>Question details (text, options, correct answer, explanation)</li>
     * </ul>
     *
     * @param file The CSV file to upload
     * @return Response containing paper details (paperId, paperName, totalQuestions, sections)
     */
    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Map<String, Object>> uploadCsv(@RequestParam("file") MultipartFile file) {
        Response<Map<String, Object>> response = new Response<>();

        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                response.setStatus(0);
                response.setMessage("No file uploaded or file is empty");
                return response;
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".CSV"))) {
                response.setStatus(0);
                response.setMessage("Invalid file type. Please upload a CSV file.");
                return response;
            }

            logger.info("Processing CSV upload: {}", filename);

            // Upload to S3 and process CSV
            Map<String, Object> result = paperCsvUploadService.processCsvAndCreatePaper(file);

            response.setStatus(1);
            response.setMessage("Paper uploaded successfully");
            response.setBody(result);

            logger.info("CSV upload successful: paperId={}, totalQuestions={}",
                result.get("paperId"), result.get("totalQuestions"));

        } catch (IllegalArgumentException e) {
            logger.error("Validation error during CSV upload: {}", e.getMessage());
            response.setStatus(0);
            response.setMessage("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing CSV upload", e);
            response.setStatus(0);
            response.setMessage("Error processing CSV: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get the CSV template format documentation.
     *
     * @return Response containing CSV format information
     */
    @PostMapping("/csv/format")
    public Response<Map<String, Object>> getCsvFormat() {
        Response<Map<String, Object>> response = new Response<>();

        Map<String, Object> format = new java.util.HashMap<>();

        format.put("columns", new String[]{
            "paperType", "paperCategory", "paperSubCategory", "paperName",
            "sectionName", "SectionType", "subSectionName",
            "questionNumber", "question", "questionImage",
            "option1", "option1_Image", "option2", "option2_Image",
            "option3", "option3_Image", "option4", "option4_Image",
            "correctOption", "answerDescription1", "answerDescriptionImage",
            "complexityLevel", "complexityScore", "type", "totalScore"
        });

        format.put("paper_categories", new String[]{"SSC_CGL", "SSC_CPO", "SSC_CHSL", "BANK_PO"});
        format.put("paper_sub_categories", new String[]{
            "SSC_CGL_TIER1", "SSC_CGL_TIER2", "SSC_CHSL_TIER1", "SSC_CHSL_TIER2",
            "SSC_CPO_TIER1", "SSC_CPO_TIER2"
        });
        format.put("section_types", new String[]{
            "GeneralIntelligence", "QuantitativeAptitude", "EnglishLanguage",
            "GeneralAwareness", "Statistics"
        });
        format.put("test_types", new String[]{"FREE", "PAID"});
        format.put("difficulty_levels", new String[]{"EASY", "MEDIUM", "HARD"});
        format.put("correct_option_values", new String[]{"A", "B", "C", "D"});

        response.setStatus(1);
        response.setMessage("CSV format information");
        response.setBody(format);

        return response;
    }
}
