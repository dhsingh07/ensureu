package com.book.ensureu.admin.service.Impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.StorageFolderConstants;
import com.book.ensureu.service.FileStorageService;
import com.book.ensureu.admin.dto.PaperCsvRowDto;
import com.book.ensureu.admin.service.PaperCsvUploadService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.QuestionSelectionType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.Options;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.Pattern;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;
import com.book.ensureu.model.QuestionData;
import com.book.ensureu.model.Sections;
import com.book.ensureu.model.Solution;
import com.book.ensureu.model.SubSections;
import com.book.ensureu.repository.FreePaperCollectionRepository;
import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Service
public class PaperCsvUploadServiceImpl implements PaperCsvUploadService {

    private static final Logger logger = LoggerFactory.getLogger(PaperCsvUploadServiceImpl.class);

    @Autowired
    private FreePaperCollectionRepository freePaperRepository;

    @Autowired
    private PaidPaperCollectionRepository paidPaperRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Map<String, Object> processCsvAndCreatePaper(MultipartFile file) throws Exception {
        String csvUrl = null;

        // Try to upload CSV to S3 for backup/audit (optional - don't fail if S3 is unavailable)
        try {
            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String csvFilename = timestamp + "_" + (originalFilename != null ? originalFilename : "paper.csv");

            // Upload to S3 in csv-uploads folder
            csvUrl = fileStorageService.uploadFile(file, StorageFolderConstants.CSV_UPLOADS, csvFilename);
            logger.info("CSV file uploaded to storage: {}", csvUrl);
        } catch (Exception e) {
            logger.warn("Failed to upload CSV to storage (proceeding without backup): {}", e.getMessage());
            // Continue without S3 backup - not critical
        }

        // Process the CSV
        Map<String, Object> result = processCsvAndCreatePaper(file.getInputStream());

        // Add CSV URL to result if upload succeeded
        if (csvUrl != null) {
            result.put("csvUrl", csvUrl);
        }

        return result;
    }

    @Override
    public Map<String, Object> processCsvAndCreatePaper(InputStream inputStream) throws Exception {
        // Parse CSV
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        CsvToBean<PaperCsvRowDto> csvToBean = new CsvToBeanBuilder<PaperCsvRowDto>(reader)
                .withType(PaperCsvRowDto.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(false) // Collect errors instead of throwing
                .build();

        List<PaperCsvRowDto> rows = csvToBean.parse();

        // Check for parsing exceptions and log them
        if (!csvToBean.getCapturedExceptions().isEmpty()) {
            StringBuilder errors = new StringBuilder();
            csvToBean.getCapturedExceptions().forEach(e ->
                errors.append("Line ").append(e.getLineNumber()).append(": ").append(e.getMessage()).append("; ")
            );
            logger.warn("CSV parsing had {} errors: {}", csvToBean.getCapturedExceptions().size(), errors);
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty or has no valid rows");
        }

        // Filter out invalid rows and validate
        List<PaperCsvRowDto> validRows = new ArrayList<>();
        int invalidCount = 0;
        for (int i = 0; i < rows.size(); i++) {
            PaperCsvRowDto row = rows.get(i);
            if (isValidRow(row)) {
                validRows.add(row);
            } else {
                invalidCount++;
                logger.warn("Skipping invalid row {}: missing required fields (paperType={}, paperName={}, question={})",
                    i + 2, row.getPaperType(), row.getPaperName(),
                    row.getQuestion() != null ? row.getQuestion().substring(0, Math.min(30, row.getQuestion().length())) : "null");
            }
        }

        if (validRows.isEmpty()) {
            throw new IllegalArgumentException("No valid rows found in CSV. Please ensure all rows have: paperType, paperCategory, paperName, sectionName, question, and options");
        }

        if (invalidCount > 0) {
            logger.info("Processed {} valid rows, skipped {} invalid rows", validRows.size(), invalidCount);
        }

        rows = validRows;

        // Get paper metadata from first row
        PaperCsvRowDto firstRow = rows.get(0);
        String paperName = firstRow.getPaperName();
        String testType = firstRow.getTestTypeOrDefault();
        boolean isFree = "FREE".equalsIgnoreCase(testType);

        logger.info("Processing CSV with {} rows for paper: {}", rows.size(), paperName);

        // Build the paper structure
        Map<String, Object> paperData = buildPaperStructure(rows, firstRow);

        // Generate paper ID
        String paperId = generatePaperId();

        // Save to appropriate collection
        if (isFree) {
            FreePaperCollection paper = createFreePaper(paperId, firstRow, paperData);
            freePaperRepository.save(paper);
            logger.info("Saved free paper with ID: {}", paperId);
        } else {
            PaidPaperCollection paper = createPaidPaper(paperId, firstRow, paperData);
            paidPaperRepository.save(paper);
            logger.info("Saved paid paper with ID: {}", paperId);
        }

        // Return result
        Map<String, Object> result = new HashMap<>();
        result.put("paperId", paperId);
        result.put("paperName", paperName);
        result.put("testType", testType);
        result.put("totalQuestions", rows.size());
        result.put("sections", ((List<?>) paperData.get("sections")).size());

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildPaperStructure(List<PaperCsvRowDto> rows, PaperCsvRowDto firstRow) {
        // Group questions by section and sub-section
        Map<String, Map<String, List<PaperCsvRowDto>>> sectionMap = new LinkedHashMap<>();

        for (PaperCsvRowDto row : rows) {
            String sectionKey = row.getSectionName();
            String subSectionKey = row.getSubSectionName() != null && !row.getSubSectionName().trim().isEmpty()
                    ? row.getSubSectionName() : "Default";

            sectionMap.computeIfAbsent(sectionKey, k -> new LinkedHashMap<>())
                    .computeIfAbsent(subSectionKey, k -> new ArrayList<>())
                    .add(row);
        }

        // Build sections list
        List<Sections<SubSections<Question<Problem>>>> sectionsList = new ArrayList<>();
        int sectionNo = 1;
        int globalQNo = 1;

        // Get default values from first row
        double perQuestionMarks = firstRow.getPerQuestionMarksOrDefault();
        double negativeMarks = firstRow.getNegativeMarksOrDefault();

        for (Map.Entry<String, Map<String, List<PaperCsvRowDto>>> sectionEntry : sectionMap.entrySet()) {
            String sectionName = sectionEntry.getKey();
            Map<String, List<PaperCsvRowDto>> subSectionMap = sectionEntry.getValue();

            // Get section type from first question in section
            PaperCsvRowDto firstQuestion = subSectionMap.values().iterator().next().get(0);
            SectionType sectionType = parseSectionType(firstQuestion.getSectionType());

            // Build sub-sections
            List<SubSections<Question<Problem>>> subSectionsList = new ArrayList<>();
            int subSectionNo = 1;
            int sectionQuestionCount = 0;

            for (Map.Entry<String, List<PaperCsvRowDto>> subEntry : subSectionMap.entrySet()) {
                String subSectionName = subEntry.getKey();
                List<PaperCsvRowDto> questions = subEntry.getValue();

                // Build questions
                List<Question<Problem>> questionList = new ArrayList<>();
                for (PaperCsvRowDto qRow : questions) {
                    Question<Problem> question = buildQuestion(qRow, globalQNo++);
                    questionList.add(question);
                }

                // Build sub-section
                SubSections<Question<Problem>> subSection = new SubSections<>();
                subSection.setTitle(subSectionName.equals("Default") ? sectionName : subSectionName);
                subSection.setSNo(subSectionNo++);
                subSection.setQuestionCount(questionList.size());
                subSection.setScore(questionList.size() * perQuestionMarks);

                QuestionData<Question<Problem>> questionData = new QuestionData<>();
                questionData.setQuestions(questionList);
                subSection.setQuestionData(questionData);

                subSectionsList.add(subSection);
                sectionQuestionCount += questionList.size();
            }

            // Build section
            Sections<SubSections<Question<Problem>>> section = new Sections<>();
            section.setId(UUID.randomUUID().toString());
            section.setTitle(sectionName);
            section.setSNo(sectionNo++);
            section.setSectionType(sectionType);
            section.setQuestionCount(sectionQuestionCount);
            section.setPerQuestionMarks(perQuestionMarks);
            section.setNegativeMarks(negativeMarks);
            section.setScore(sectionQuestionCount * perQuestionMarks);
            section.setSubSections(subSectionsList);

            sectionsList.add(section);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sections", sectionsList);
        result.put("totalQuestions", rows.size());

        return result;
    }

    private Question<Problem> buildQuestion(PaperCsvRowDto row, int qNo) {
        Question<Problem> question = new Question<>();
        question.setId("q_" + qNo);
        question.setqNo((long) qNo);
        question.setType(row.getType() != null ? row.getType() : "mcq");
        question.setQuestionType(QuestionSelectionType.RADIOBUTTON);
        question.setComplexityLevel(row.getComplexityLevel() != null ? row.getComplexityLevel() : "MEDIUM");
        question.setComplexityScore(row.getComplexityScore() > 0 ? row.getComplexityScore() : 5);
        question.setMinTimeInSecond(60);
        question.setMaxTimeInSecond(180);
        question.setAverageTimeSecond(120);

        // Build problem
        Problem problem = buildProblem(row);
        question.setProblem(problem);

        return question;
    }

    private Problem buildProblem(PaperCsvRowDto row) {
        Problem problem = new Problem();

        // Set question text
        problem.setValue(row.getQuestion());
        problem.setQuestion(row.getQuestion());

        // Set question image
        if (row.getQuestionImage() != null && !row.getQuestionImage().trim().isEmpty()) {
            problem.setImage(row.getQuestionImage());
        }

        // Build options (numbered 1-4)
        List<Options> options = new ArrayList<>();

        Options opt1 = new Options();
        opt1.setPrompt("1");
        opt1.setValue(row.getOption1());
        opt1.setText(row.getOption1());
        if (row.getOption1Image() != null && !row.getOption1Image().trim().isEmpty()) {
            opt1.setImage(row.getOption1Image());
        }
        options.add(opt1);

        Options opt2 = new Options();
        opt2.setPrompt("2");
        opt2.setValue(row.getOption2());
        opt2.setText(row.getOption2());
        if (row.getOption2Image() != null && !row.getOption2Image().trim().isEmpty()) {
            opt2.setImage(row.getOption2Image());
        }
        options.add(opt2);

        Options opt3 = new Options();
        opt3.setPrompt("3");
        opt3.setValue(row.getOption3());
        opt3.setText(row.getOption3());
        if (row.getOption3Image() != null && !row.getOption3Image().trim().isEmpty()) {
            opt3.setImage(row.getOption3Image());
        }
        options.add(opt3);

        Options opt4 = new Options();
        opt4.setPrompt("4");
        opt4.setValue(row.getOption4());
        opt4.setText(row.getOption4());
        if (row.getOption4Image() != null && !row.getOption4Image().trim().isEmpty()) {
            opt4.setImage(row.getOption4Image());
        }
        options.add(opt4);

        problem.setOptions(options);

        // Set correct option (already in 1,2,3,4 format)
        problem.setCo(Arrays.asList(row.getCorrectOptionIndex()));
        problem.setSo(Collections.emptyList());

        // Set solution/answer description
        if (row.getAnswerDescription1() != null && !row.getAnswerDescription1().trim().isEmpty()) {
            problem.setSolution(row.getAnswerDescription1());

            Solution solution = new Solution();
            solution.setValue(row.getAnswerDescription1());
            solution.setAddedon(String.valueOf(System.currentTimeMillis()));
            if (row.getAnswerDescriptionImage() != null && !row.getAnswerDescriptionImage().trim().isEmpty()) {
                solution.setImage(row.getAnswerDescriptionImage());
            }
            problem.setSolutions(Arrays.asList(solution));
        }

        return problem;
    }

    @SuppressWarnings("unchecked")
    private FreePaperCollection createFreePaper(String paperId, PaperCsvRowDto firstRow, Map<String, Object> paperData) {
        FreePaperCollection paper = new FreePaperCollection();
        paper.setId(paperId);
        paper.setPaperName(firstRow.getPaperName());
        paper.setPaperType(parsePaperType(firstRow.getPaperType()));
        paper.setPaperCategory(parsePaperCategory(firstRow.getPaperCategory()));
        paper.setPaperSubCategory(parsePaperSubCategory(firstRow.getPaperSubCategory()));
        paper.setPaperSubCategoryName(firstRow.getPaperSubCategory());
        paper.setTestType(TestType.FREE);
        paper.setTotalTime((long) firstRow.getTotalTimeMinutesOrDefault() * 60);
        paper.setPerQuestionScore(firstRow.getPerQuestionMarksOrDefault());
        paper.setNegativeMarks(firstRow.getNegativeMarksOrDefault());
        paper.setTotalQuestionCount((int) paperData.get("totalQuestions"));
        paper.setTotalScore(firstRow.getTotalScore() > 0 ? firstRow.getTotalScore()
                : (int) paperData.get("totalQuestions") * firstRow.getPerQuestionMarksOrDefault());
        paper.setPaperStateStatus(PaperStateStatus.DRAFT);
        paper.setCreateDateTime(System.currentTimeMillis());
        paper.setValidityRangeStartDateTime(System.currentTimeMillis());
        paper.setValidityRangeEndDateTime(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

        // Build pattern
        Pattern<Sections<SubSections<Question<Problem>>>> pattern = new Pattern<>();
        pattern.setId(UUID.randomUUID().toString());
        pattern.setTitle(firstRow.getPaperName());
        pattern.setTime((long) firstRow.getTotalTimeMinutesOrDefault() * 60);
        pattern.setPaperType(parsePaperType(firstRow.getPaperType()));
        pattern.setCreatedOn(String.valueOf(System.currentTimeMillis()));
        pattern.setSections((List<Sections<SubSections<Question<Problem>>>>) paperData.get("sections"));

        paper.setPattern(pattern);

        return paper;
    }

    @SuppressWarnings("unchecked")
    private PaidPaperCollection createPaidPaper(String paperId, PaperCsvRowDto firstRow, Map<String, Object> paperData) {
        PaidPaperCollection paper = new PaidPaperCollection();
        paper.setId(paperId);
        paper.setPaperName(firstRow.getPaperName());
        paper.setPaperType(parsePaperType(firstRow.getPaperType()));
        paper.setPaperCategory(parsePaperCategory(firstRow.getPaperCategory()));
        paper.setPaperSubCategory(parsePaperSubCategory(firstRow.getPaperSubCategory()));
        paper.setPaperSubCategoryName(firstRow.getPaperSubCategory());
        paper.setTestType(TestType.PAID);
        paper.setTotalTime((long) firstRow.getTotalTimeMinutesOrDefault() * 60);
        paper.setPerQuestionScore(firstRow.getPerQuestionMarksOrDefault());
        paper.setNegativeMarks(firstRow.getNegativeMarksOrDefault());
        paper.setTotalQuestionCount((int) paperData.get("totalQuestions"));
        paper.setTotalScore(firstRow.getTotalScore() > 0 ? firstRow.getTotalScore()
                : (int) paperData.get("totalQuestions") * firstRow.getPerQuestionMarksOrDefault());
        paper.setPaperStateStatus(PaperStateStatus.DRAFT);
        paper.setCreateDateTime(System.currentTimeMillis());
        paper.setValidityRangeStartDateTime(System.currentTimeMillis());
        paper.setValidityRangeEndDateTime(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

        // Build pattern
        Pattern<Sections<SubSections<Question<Problem>>>> pattern = new Pattern<>();
        pattern.setId(UUID.randomUUID().toString());
        pattern.setTitle(firstRow.getPaperName());
        pattern.setTime((long) firstRow.getTotalTimeMinutesOrDefault() * 60);
        pattern.setPaperType(parsePaperType(firstRow.getPaperType()));
        pattern.setCreatedOn(String.valueOf(System.currentTimeMillis()));
        pattern.setSections((List<Sections<SubSections<Question<Problem>>>>) paperData.get("sections"));

        paper.setPattern(pattern);

        return paper;
    }

    private String generatePaperId() {
        return "csv_" + System.currentTimeMillis();
    }

    private PaperType parsePaperType(String paperType) {
        try {
            return PaperType.valueOf(paperType);
        } catch (Exception e) {
            logger.warn("Unknown paper type: {}, defaulting to SSC", paperType);
            return PaperType.SSC;
        }
    }

    private PaperCategory parsePaperCategory(String category) {
        try {
            return PaperCategory.valueOf(category);
        } catch (Exception e) {
            logger.warn("Unknown paper category: {}, defaulting to SSC_CGL", category);
            return PaperCategory.SSC_CGL;
        }
    }

    private PaperSubCategory parsePaperSubCategory(String subCategory) {
        try {
            return PaperSubCategory.valueOf(subCategory);
        } catch (Exception e) {
            logger.warn("Unknown paper sub-category: {}, defaulting to SSC_CGL_TIER1", subCategory);
            return PaperSubCategory.SSC_CGL_TIER1;
        }
    }

    private SectionType parseSectionType(String sectionType) {
        try {
            return SectionType.valueOf(sectionType);
        } catch (Exception e) {
            logger.warn("Unknown section type: {}, defaulting to GeneralIntelligence", sectionType);
            return SectionType.GeneralIntelligence;
        }
    }

    /**
     * Validate that a row has all required fields
     */
    private boolean isValidRow(PaperCsvRowDto row) {
        if (row == null) return false;
        // Check required fields
        return isNotEmpty(row.getPaperType()) &&
               isNotEmpty(row.getPaperCategory()) &&
               isNotEmpty(row.getPaperName()) &&
               isNotEmpty(row.getSectionName()) &&
               isNotEmpty(row.getQuestion()) &&
               isNotEmpty(row.getOption1()) &&
               isNotEmpty(row.getOption2()) &&
               isNotEmpty(row.getOption3()) &&
               isNotEmpty(row.getOption4());
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
