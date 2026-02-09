package com.book.ensureu.dto;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.QuestionVaultType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;

public class QuestionVaultDto extends BaseQuestionVault {

	private String paperId;
	private String questionId;
	private String sectionName;
	private SectionType sectionType;
	private Question<Problem> question;
	private QuestionVaultType questionType;
	private String reasone;
	private String userName;
	private long createdDate;

	public QuestionVaultDto() {
		super();
	}

	public QuestionVaultDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType) {
		super(id, paperType, paperCategory, paperSubCategory, testType);
	}

	public QuestionVaultDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperId, String questionId,
			Question<Problem> question) {
		super(id, paperType, paperCategory, paperSubCategory, testType);
		this.paperId = paperId;
		this.questionId = questionId;
		this.question = question;
	}

	public QuestionVaultDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperId, String questionId, String sectionName,
			SectionType sectionType, Question<Problem> question) {
		super(id, paperType, paperCategory, paperSubCategory, testType);
		this.paperId = paperId;
		this.questionId = questionId;
		this.sectionName = sectionName;
		this.sectionType = sectionType;
		this.question = question;
	}

	public QuestionVaultDto(String id, PaperType paperType, PaperCategory paperCategory,
			PaperSubCategory paperSubCategory, TestType testType, String paperId, String questionId, String sectionName,
			SectionType sectionType, Question<Problem> question, QuestionVaultType questionType, String reasone,
			String userName, long createdDate) {
		super(id, paperType, paperCategory, paperSubCategory, testType);
		this.paperId = paperId;
		this.questionId = questionId;
		this.sectionName = sectionName;
		this.sectionType = sectionType;
		this.question = question;
		this.questionType = questionType;
		this.reasone = reasone;
		this.userName = userName;
		this.createdDate=createdDate;
	}

	public String getPaperId() {
		return paperId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public Question<Problem> getQuestion() {
		return question;
	}

	public String getSectionName() {
		return sectionName;
	}

	public SectionType getSectionType() {
		return sectionType;
	}

	public QuestionVaultType getQuestionType() {
		return questionType;
	}

	public String getReasone() {
		return reasone;
	}

	public String getUserName() {
		return userName;
	}
	
	

}
