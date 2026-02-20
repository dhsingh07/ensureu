package com.book.ensureu.admin.dto;

import com.opencsv.bean.CsvBindByName;

/**
 * DTO for mapping CSV rows when uploading SSC papers.
 * Each row represents one question with paper/section metadata.
 */
public class PaperCsvRowDto {

    // Paper-level fields
    @CsvBindByName(column = "paperType", required = true)
    private String paperType;

    @CsvBindByName(column = "paperCategory", required = true)
    private String paperCategory;

    @CsvBindByName(column = "paperSubCategory", required = true)
    private String paperSubCategory;

    @CsvBindByName(column = "paperName", required = true)
    private String paperName;

    // Section-level fields
    @CsvBindByName(column = "sectionName", required = true)
    private String sectionName;

    @CsvBindByName(column = "SectionType", required = true)
    private String sectionType;

    @CsvBindByName(column = "subSectionName")
    private String subSectionName;

    // Question fields
    @CsvBindByName(column = "questionNumber", required = true)
    private int questionNumber;

    @CsvBindByName(column = "question", required = true)
    private String question;

    @CsvBindByName(column = "questionImage")
    private String questionImage;

    // Options (numbered 1-4)
    @CsvBindByName(column = "option1", required = true)
    private String option1;

    @CsvBindByName(column = "option1_Image")
    private String option1Image;

    @CsvBindByName(column = "option2", required = true)
    private String option2;

    @CsvBindByName(column = "option2_Image")
    private String option2Image;

    @CsvBindByName(column = "option3", required = true)
    private String option3;

    @CsvBindByName(column = "option3_Image")
    private String option3Image;

    @CsvBindByName(column = "option4", required = true)
    private String option4;

    @CsvBindByName(column = "option4_Image")
    private String option4Image;

    // Correct option (1, 2, 3, or 4)
    @CsvBindByName(column = "correctOption", required = true)
    private String correctOption;

    // Solution/Answer
    @CsvBindByName(column = "answerDescription1")
    private String answerDescription1;

    @CsvBindByName(column = "answerDescriptionImage")
    private String answerDescriptionImage;

    // Complexity
    @CsvBindByName(column = "complexityLevel")
    private String complexityLevel;

    @CsvBindByName(column = "complexityScore")
    private int complexityScore;

    // Question type
    @CsvBindByName(column = "type")
    private String type;

    // Total score for the paper
    @CsvBindByName(column = "totalScore")
    private double totalScore;

    // Test type (FREE/PAID) - optional, defaults to FREE
    @CsvBindByName(column = "testType")
    private String testType;

    // Time in minutes - optional, defaults to 60
    @CsvBindByName(column = "totalTimeMinutes")
    private int totalTimeMinutes;

    // Per question marks - optional, defaults to 2
    @CsvBindByName(column = "perQuestionMarks")
    private double perQuestionMarks;

    // Negative marks - optional, defaults to 0.5
    @CsvBindByName(column = "negativeMarks")
    private double negativeMarks;

    // Getters and Setters
    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getPaperCategory() {
        return paperCategory;
    }

    public void setPaperCategory(String paperCategory) {
        this.paperCategory = paperCategory;
    }

    public String getPaperSubCategory() {
        return paperSubCategory;
    }

    public void setPaperSubCategory(String paperSubCategory) {
        this.paperSubCategory = paperSubCategory;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public String getSubSectionName() {
        return subSectionName;
    }

    public void setSubSectionName(String subSectionName) {
        this.subSectionName = subSectionName;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption1Image() {
        return option1Image;
    }

    public void setOption1Image(String option1Image) {
        this.option1Image = option1Image;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption2Image() {
        return option2Image;
    }

    public void setOption2Image(String option2Image) {
        this.option2Image = option2Image;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption3Image() {
        return option3Image;
    }

    public void setOption3Image(String option3Image) {
        this.option3Image = option3Image;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption4Image() {
        return option4Image;
    }

    public void setOption4Image(String option4Image) {
        this.option4Image = option4Image;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public String getAnswerDescription1() {
        return answerDescription1;
    }

    public void setAnswerDescription1(String answerDescription1) {
        this.answerDescription1 = answerDescription1;
    }

    public String getAnswerDescriptionImage() {
        return answerDescriptionImage;
    }

    public void setAnswerDescriptionImage(String answerDescriptionImage) {
        this.answerDescriptionImage = answerDescriptionImage;
    }

    public String getComplexityLevel() {
        return complexityLevel;
    }

    public void setComplexityLevel(String complexityLevel) {
        this.complexityLevel = complexityLevel;
    }

    public int getComplexityScore() {
        return complexityScore;
    }

    public void setComplexityScore(int complexityScore) {
        this.complexityScore = complexityScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public int getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(int totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public double getPerQuestionMarks() {
        return perQuestionMarks;
    }

    public void setPerQuestionMarks(double perQuestionMarks) {
        this.perQuestionMarks = perQuestionMarks;
    }

    public double getNegativeMarks() {
        return negativeMarks;
    }

    public void setNegativeMarks(double negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    /**
     * Get correct option as string (already in 1,2,3,4 format)
     */
    public String getCorrectOptionIndex() {
        if (correctOption == null) return "1";
        return correctOption.trim();
    }

    /**
     * Get test type with default
     */
    public String getTestTypeOrDefault() {
        return (testType != null && !testType.trim().isEmpty()) ? testType : "FREE";
    }

    /**
     * Get total time with default (60 minutes)
     */
    public int getTotalTimeMinutesOrDefault() {
        return totalTimeMinutes > 0 ? totalTimeMinutes : 60;
    }

    /**
     * Get per question marks with default (2 marks)
     */
    public double getPerQuestionMarksOrDefault() {
        return perQuestionMarks > 0 ? perQuestionMarks : 2.0;
    }

    /**
     * Get negative marks with default (0.5)
     */
    public double getNegativeMarksOrDefault() {
        return negativeMarks > 0 ? negativeMarks : 0.5;
    }
}
