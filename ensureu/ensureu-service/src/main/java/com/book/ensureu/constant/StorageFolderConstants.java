package com.book.ensureu.constant;

/**
 * EnsureU - Assessment Platform for Competitive Exams
 * A product of GrayscaleLabs AI Pvt Ltd.
 *
 * Constants for S3/Storage folder structure.
 *
 * Bucket Structure:
 * ensureu-assets/
 * ├── papers/
 * │   ├── ssc-cgl/
 * │   │   ├── free/
 * │   │   │   └── {paperId}/
 * │   │   │       ├── cover.jpg
 * │   │   │       └── questions/
 * │   │   │           ├── q1-problem.png
 * │   │   │           ├── q1-option-a.png
 * │   │   │           └── ...
 * │   │   └── paid/
 * │   │       └── {paperId}/
 * │   ├── ssc-chsl/
 * │   ├── ssc-cpo/
 * │   ├── bank-po/
 * │   └── bank-clerk/
 * ├── question-bank/
 * │   ├── {category}/
 * │   │   └── {questionId}/
 * │   │       ├── problem.png
 * │   │       ├── problem-hindi.png
 * │   │       ├── option-a.png
 * │   │       ├── option-b.png
 * │   │       ├── option-c.png
 * │   │       ├── option-d.png
 * │   │       └── solution.png
 * ├── source-materials/
 * │   ├── pdfs/
 * │   │   └── {bookId}/
 * │   │       └── book.pdf
 * │   └── references/
 * ├── generated-content/
 * │   ├── ai-questions/
 * │   │   └── {batchId}/
 * │   └── ai-solutions/
 * ├── user-content/
 * │   ├── profiles/
 * │   │   └── {userId}/
 * │   └── uploads/
 * └── blog/
 *     ├── covers/
 *     └── content/
 */
public final class StorageFolderConstants {

    private StorageFolderConstants() {
        // Prevent instantiation
    }

    // Root folders
    public static final String PAPERS = "papers";
    public static final String QUESTION_BANK = "question-bank";
    public static final String SOURCE_MATERIALS = "source-materials";
    public static final String GENERATED_CONTENT = "generated-content";
    public static final String USER_CONTENT = "user-content";
    public static final String BLOG = "blog";
    public static final String CSV_UPLOADS = "csv-uploads";

    // Paper sub-folders
    public static final String FREE = "free";
    public static final String PAID = "paid";
    public static final String QUESTIONS = "questions";

    // Paper categories (lowercase for URLs)
    public static final String SSC_CGL = "ssc-cgl";
    public static final String SSC_CHSL = "ssc-chsl";
    public static final String SSC_CPO = "ssc-cpo";
    public static final String BANK_PO = "bank-po";
    public static final String BANK_CLERK = "bank-clerk";

    // Source materials sub-folders
    public static final String PDFS = "pdfs";
    public static final String REFERENCES = "references";

    // Generated content sub-folders
    public static final String AI_QUESTIONS = "ai-questions";
    public static final String AI_SOLUTIONS = "ai-solutions";

    // User content sub-folders
    public static final String PROFILES = "profiles";
    public static final String USER_UPLOADS = "uploads";

    // Blog sub-folders
    public static final String COVERS = "covers";
    public static final String CONTENT = "content";

    // Image types for questions
    public static final String PROBLEM_IMAGE = "problem";
    public static final String PROBLEM_HINDI_IMAGE = "problem-hindi";
    public static final String OPTION_A = "option-a";
    public static final String OPTION_B = "option-b";
    public static final String OPTION_C = "option-c";
    public static final String OPTION_D = "option-d";
    public static final String SOLUTION_IMAGE = "solution";

    /**
     * Build paper folder path
     * @param category Paper category (e.g., SSC_CGL)
     * @param isPaid true for paid papers
     * @param paperId Paper ID
     * @return Folder path like "papers/ssc-cgl/free/{paperId}"
     */
    public static String buildPaperFolder(String category, boolean isPaid, String paperId) {
        String categoryFolder = categoryToFolderName(category);
        String typeFolder = isPaid ? PAID : FREE;
        return String.format("%s/%s/%s/%s", PAPERS, categoryFolder, typeFolder, paperId);
    }

    /**
     * Build paper questions folder path
     * @param category Paper category
     * @param isPaid true for paid papers
     * @param paperId Paper ID
     * @return Folder path like "papers/ssc-cgl/free/{paperId}/questions"
     */
    public static String buildPaperQuestionsFolder(String category, boolean isPaid, String paperId) {
        return buildPaperFolder(category, isPaid, paperId) + "/" + QUESTIONS;
    }

    /**
     * Build question bank folder path
     * @param category Paper category
     * @param questionId Question ID
     * @return Folder path like "question-bank/ssc-cgl/{questionId}"
     */
    public static String buildQuestionBankFolder(String category, String questionId) {
        String categoryFolder = categoryToFolderName(category);
        return String.format("%s/%s/%s", QUESTION_BANK, categoryFolder, questionId);
    }

    /**
     * Build source materials PDF folder
     * @param bookId Book/document ID
     * @return Folder path like "source-materials/pdfs/{bookId}"
     */
    public static String buildSourcePdfFolder(String bookId) {
        return String.format("%s/%s/%s", SOURCE_MATERIALS, PDFS, bookId);
    }

    /**
     * Build AI generated content folder
     * @param batchId Generation batch ID
     * @return Folder path like "generated-content/ai-questions/{batchId}"
     */
    public static String buildAiQuestionsFolder(String batchId) {
        return String.format("%s/%s/%s", GENERATED_CONTENT, AI_QUESTIONS, batchId);
    }

    /**
     * Build user profile folder
     * @param userId User ID
     * @return Folder path like "user-content/profiles/{userId}"
     */
    public static String buildUserProfileFolder(String userId) {
        return String.format("%s/%s/%s", USER_CONTENT, PROFILES, userId);
    }

    /**
     * Build blog content folder
     * @param blogId Blog post ID
     * @return Folder path like "blog/content/{blogId}"
     */
    public static String buildBlogContentFolder(String blogId) {
        return String.format("%s/%s/%s", BLOG, CONTENT, blogId);
    }

    /**
     * Build CSV uploads folder path
     * @param category Paper category
     * @return Folder path like "csv-uploads/ssc-cgl"
     */
    public static String buildCsvUploadsFolder(String category) {
        String categoryFolder = categoryToFolderName(category);
        return String.format("%s/%s", CSV_UPLOADS, categoryFolder);
    }

    /**
     * Convert category enum value to folder name
     * @param category Category enum value (e.g., SSC_CGL)
     * @return Folder name (e.g., ssc-cgl)
     */
    public static String categoryToFolderName(String category) {
        if (category == null) {
            return "general";
        }
        return category.toLowerCase().replace("_", "-");
    }

    /**
     * Build question image filename
     * @param questionNumber Question number
     * @param imageType Image type (problem, option-a, etc.)
     * @param extension File extension (jpg, png, etc.)
     * @return Filename like "q1-problem.png"
     */
    public static String buildQuestionImageFilename(int questionNumber, String imageType, String extension) {
        return String.format("q%d-%s.%s", questionNumber, imageType, extension);
    }
}
