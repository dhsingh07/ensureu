package com.book.ensureu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.QuestionVaultType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;

/**
 * @author dharmendra.singh
 *
 */
@Document(collection = "vault")
public class VaultModel {
	
	@Id
	private String id;
	private String userName;
	private String paperId;
	private PaperType paperType;
	private PaperCategory paperCategory;
	private PaperSubCategory paperSubCategory;
	private TestType testType;
	private SectionType sectionType;
	private String sectionName;
	private String subSectionName;
	private String questionId;
	private Question<Problem> question;
	private QuestionVaultType questionType; 
	private String reasone;
	private long createdDate;

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getQuestionId() {
		return questionId;
	}

	/**
	 * @param questionId
	 */
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	/**
	 * @return
	 */
	public PaperType getPaperType() {
		return paperType;
	}

	/**
	 * @param paperType
	 */
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}

	/**
	 * @return
	 */
	public PaperCategory getPaperCategory() {
		return paperCategory;
	}

	/**
	 * @param paperCategory
	 */
	public void setPaperCategory(PaperCategory paperCategory) {
		this.paperCategory = paperCategory;
	}

	/**
	 * @return
	 */
	public PaperSubCategory getPaperSubCategory() {
		return paperSubCategory;
	}
	
	/**
	 * @param paperSubCategory
	 */
	public void setPaperSubCategory(PaperSubCategory paperSubCategory) {
		this.paperSubCategory = paperSubCategory;
	}

	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userId
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return
	 */
	public String getPaperId() {
		return paperId;
	}

	/**
	 * @param paperId
	 */
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}

	/**
	 * @return
	 */
	public TestType getTestType() {
		return testType;
	}

	/**
	 * @param testType
	 */
	public void setTestType(TestType testType) {
		this.testType = testType;
	}

	/**
	 * @return
	 */
	public String getSectionName() {
		return sectionName;
	}

	/**
	 * @param sectionName
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	/**
	 * @return
	 */
	public String getSubSectionName() {
		return subSectionName;
	}

	/**
	 * @param subSectionName
	 */
	public void setSubSectionName(String subSectionName) {
		this.subSectionName = subSectionName;
	}

	/**
	 * @return
	 */
	public Question<Problem> getQuestion() {
		return question;
	}

	/**
	 * @param question
	 */
	public void setQuestion(Question<Problem> question) {
		this.question = question;
	}

	/**
	 * @return
	 */
	public SectionType getSectionType() {
		return sectionType;
	}

	/**
	 * @param sectionType
	 */
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}

	public QuestionVaultType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionVaultType questionType) {
		this.questionType = questionType;
	}

	public String getReasone() {
		return reasone;
	}

	public void setReasone(String reasone) {
		this.reasone = reasone;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "VaultModel [id=" + id + ", userName=" + userName + ", paperId=" + paperId + ", paperType=" + paperType
				+ ", paperCategory=" + paperCategory + ", paperSubCategory=" + paperSubCategory + ", testType="
				+ testType + ", sectionType=" + sectionType + ", sectionName=" + sectionName + ", subSectionName="
				+ subSectionName + ", questionId=" + questionId + ", question=" + question + ", questionType="
				+ questionType + ", reasone=" + reasone + ", createdDate=" + createdDate + "]";
	}
	
	

	
}
