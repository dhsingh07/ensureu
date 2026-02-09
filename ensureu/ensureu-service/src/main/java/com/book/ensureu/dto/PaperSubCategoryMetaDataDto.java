package com.book.ensureu.dto;

public class PaperSubCategoryMetaDataDto {

	private String paperSubCateory;
	private int paperSubCategoryCountFree;
	private int paperSubCategoryCountPaid;
	private int paperSubCategoryCountQuiz;
	private int paperSubCategoryCountPractice;
	
	
	public PaperSubCategoryMetaDataDto() {
		super();
	}


	public PaperSubCategoryMetaDataDto(String paperSubCateory, int paperSubCategoryCountFree,
			int paperSubCategoryCountPaid) {
		super();
		this.paperSubCateory = paperSubCateory;
		this.paperSubCategoryCountFree = paperSubCategoryCountFree;
		this.paperSubCategoryCountPaid = paperSubCategoryCountPaid;
	}


	public PaperSubCategoryMetaDataDto(String paperSubCateory, int paperSubCategoryCountFree,
			int paperSubCategoryCountPaid, int paperSubCategoryCountQuiz, int paperSubCategoryCountPractice) {
		super();
		this.paperSubCateory = paperSubCateory;
		this.paperSubCategoryCountFree = paperSubCategoryCountFree;
		this.paperSubCategoryCountPaid = paperSubCategoryCountPaid;
		this.paperSubCategoryCountQuiz = paperSubCategoryCountQuiz;
		this.paperSubCategoryCountPractice = paperSubCategoryCountPractice;
	}


	public String getPaperSubCateory() {
		return paperSubCateory;
	}


	public int getPaperSubCategoryCountFree() {
		return paperSubCategoryCountFree;
	}


	public int getPaperSubCategoryCountPaid() {
		return paperSubCategoryCountPaid;
	}


	public int getPaperSubCategoryCountQuiz() {
		return paperSubCategoryCountQuiz;
	}


	public int getPaperSubCategoryCountPractice() {
		return paperSubCategoryCountPractice;
	}


	@Override
	public String toString() {
		return "PaperSubCategoryMetaDataDto [paperSubCateory=" + paperSubCateory + ", paperSubCategoryCountFree="
				+ paperSubCategoryCountFree + ", paperSubCategoryCountPaid=" + paperSubCategoryCountPaid
				+ ", paperSubCategoryCountQuiz=" + paperSubCategoryCountQuiz + ", paperSubCategoryCountPractice="
				+ paperSubCategoryCountPractice + "]";
	}
	
	
	
	
}
