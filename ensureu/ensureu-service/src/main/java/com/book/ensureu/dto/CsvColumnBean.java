package com.book.ensureu.dto;

import java.io.Serializable;

import com.book.ensureu.constant.SectionType;
import com.opencsv.bean.CsvBindByName;

public class CsvColumnBean extends CsvBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6418381187632967863L;
	@CsvBindByName
	private String paperName;
	@CsvBindByName
	private String sectionName;
	@CsvBindByName
	private SectionType sectionType;
	@CsvBindByName
	private String subSectionName;
	@CsvBindByName
	private int questionNumber;
	@CsvBindByName
	private String question;
	@CsvBindByName
	private String questionImage;
	@CsvBindByName
	private String option1;
	@CsvBindByName
	private String option1_Image;
	@CsvBindByName
	private String option2;
	@CsvBindByName
	private String option2_Image;
	@CsvBindByName
	private String option3;
	@CsvBindByName
	private String option3_Image;
	@CsvBindByName
	private String option4;
	@CsvBindByName
	private String option4_Image;
	@CsvBindByName
	private int correctOption;
	@CsvBindByName
	private String answerDescription1;
	@CsvBindByName
	private String answerDescription2;
	@CsvBindByName
	private String answerDescriptionImage;
	@CsvBindByName
	private String complexityLevel;
	@CsvBindByName
	private int complexityScore;
	@CsvBindByName
	private String type;
	
	
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
	public SectionType getSectionType() {
		return sectionType;
	}
	public void setSectionType(SectionType sectionType) {
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
	public String getOption1_Image() {
		return option1_Image;
	}
	public void setOption1_Image(String option1_Image) {
		this.option1_Image = option1_Image;
	}
	public String getOption2() {
		return option2;
	}
	public void setOption2(String option2) {
		this.option2 = option2;
	}
	public String getOption2_Image() {
		return option2_Image;
	}
	public void setOption2_Image(String option2_Image) {
		this.option2_Image = option2_Image;
	}
	public String getOption3() {
		return option3;
	}
	public void setOption3(String option3) {
		this.option3 = option3;
	}
	public String getOption3_Image() {
		return option3_Image;
	}
	public void setOption3_Image(String option3_Image) {
		this.option3_Image = option3_Image;
	}
	public String getOption4() {
		return option4;
	}
	public void setOption4(String option4) {
		this.option4 = option4;
	}
	public String getOption4_Image() {
		return option4_Image;
	}
	public void setOption4_Image(String option4_Image) {
		this.option4_Image = option4_Image;
	}
	public int getCorrectOption() {
		return correctOption;
	}
	public void setCorrectOption(int correctOption) {
		this.correctOption = correctOption;
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
	public String getAnswerDescription1() {
		return answerDescription1;
	}
	public void setAnswerDescription1(String answerDescription1) {
		this.answerDescription1 = answerDescription1;
	}
	public String getAnswerDescription2() {
		return answerDescription2;
	}
	public void setAnswerDescription2(String answerDescription2) {
		this.answerDescription2 = answerDescription2;
	}
	
	
}
