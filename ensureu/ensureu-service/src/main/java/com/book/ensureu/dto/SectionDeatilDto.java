package com.book.ensureu.dto;

import com.book.ensureu.constant.SectionType;

public class SectionDeatilDto {
	
	private int SNo;
	private String title;
	private int questionCount;
	private SectionType sectionType;
	private double scoreInSection;
	private double negativeMarks;
	private long totalTime;
	private double perQuestionMarks;
	private String sectionName;
	
	
	public SectionDeatilDto() {
		super();
	}
	public SectionDeatilDto(int sNo, String title, int questionCount, SectionType sectionType, double scoreInSection,
			double negativeMarks, long totalTime) {
		super();
		SNo = sNo;
		this.title = title;
		this.questionCount = questionCount;
		this.sectionType = sectionType;
		this.scoreInSection = scoreInSection;
		this.negativeMarks = negativeMarks;
		this.totalTime = totalTime;
	}
	
	
	public SectionDeatilDto(int sNo, String title, int questionCount, SectionType sectionType, double scoreInSection,
			double negativeMarks, long totalTime, double perQuestionMarks) {
		super();
		SNo = sNo;
		this.title = title;
		this.questionCount = questionCount;
		this.sectionType = sectionType;
		this.scoreInSection = scoreInSection;
		this.negativeMarks = negativeMarks;
		this.totalTime = totalTime;
		this.perQuestionMarks = perQuestionMarks;
	}
	
	
	public SectionDeatilDto(int sNo, String title, int questionCount, SectionType sectionType, double scoreInSection,
			double negativeMarks, long totalTime, double perQuestionMarks, String sectionName) {
		super();
		SNo = sNo;
		this.title = title;
		this.questionCount = questionCount;
		this.sectionType = sectionType;
		this.scoreInSection = scoreInSection;
		this.negativeMarks = negativeMarks;
		this.totalTime = totalTime;
		this.perQuestionMarks = perQuestionMarks;
		this.sectionName = sectionName;
	}
	public int getSNo() {
		return SNo;
	}
	public String getTitle() {
		return title;
	}
	public int getQuestionCount() {
		return questionCount;
	}
	public SectionType getSectionType() {
		return sectionType;
	}
	public double getScoreInSection() {
		return scoreInSection;
	}
	public double getNegativeMarks() {
		return negativeMarks;
	}
	public long getTotalTime() {
		return totalTime;
	}
	
	public double getPerQuestionMarks() {
		return perQuestionMarks;
	}
	
	public String getSectionName() {
		return sectionName;
	}
	@Override
	public String toString() {
		return "SectionDeatilDto [SNo=" + SNo + ", title=" + title + ", questionCount=" + questionCount
				+ ", sectionType=" + sectionType + ", scoreInSection=" + scoreInSection + ", negativeMarks="
				+ negativeMarks + ", totalTime=" + totalTime + ", perQuestionMarks=" + perQuestionMarks + "]";
	}
	
	
}
