package com.book.ensureu.dto;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.TestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class AttemptedPaperDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -39492710882079448L;
	private Long id;
	private Long paperId;
	private String userId;
	private PaperStatus paperStatus;
	private int totalAttemptedQuestionCount;
	
	private TestType testPaper;
	
	private int totalSkipedCount;
	private int totalCorrectCount;
	private int totalInCorrectCount;

	private Long startTestTime;
	private Long endTestTime;
	private Double totalScore;
	private Long totalTimeTaken;
	
	private List<SectionResultDto> sectionVsSectionRes=new ArrayList<>();
	
	
	public AttemptedPaperDto() {
		super();
	}
	public AttemptedPaperDto(Long id, Long paperId, String userId, PaperStatus paperStatus,
			int totalAttemptedQuestionCount, TestType typePaper, int totalSkipedCount, int totalCorrectCount,
			int totalInCorrectCount, Long startTestTime, Long endTestTime, Double totalScore, Long totalTimeTaken,
			List<SectionResultDto> sectionVsSectionRes) {
		super();
		this.id = id;
		this.paperId = paperId;
		this.userId = userId;
		this.paperStatus = paperStatus;
		this.totalAttemptedQuestionCount = totalAttemptedQuestionCount;
		this.testPaper = typePaper;
		this.totalSkipedCount = totalSkipedCount;
		this.totalCorrectCount = totalCorrectCount;
		this.totalInCorrectCount = totalInCorrectCount;
		this.startTestTime = startTestTime;
		this.endTestTime = endTestTime;
		this.totalScore = totalScore;
		this.totalTimeTaken = totalTimeTaken;
		this.sectionVsSectionRes = sectionVsSectionRes;
	}
	public Long getId() {
		return id;
	}
	public Long getPaperId() {
		return paperId;
	}
	public String getUserId() {
		return userId;
	}
	public PaperStatus getPaperStatus() {
		return paperStatus;
	}
	public int getTotalAttemptedQuestionCount() {
		return totalAttemptedQuestionCount;
	}
	public TestType getTypePaper() {
		return testPaper;
	}
	public int getTotalSkipedCount() {
		return totalSkipedCount;
	}
	public int getTotalCorrectCount() {
		return totalCorrectCount;
	}
	public int getTotalInCorrectCount() {
		return totalInCorrectCount;
	}
	public Long getStartTestTime() {
		return startTestTime;
	}
	public Long getEndTestTime() {
		return endTestTime;
	}
	public Double getTotalScore() {
		return totalScore;
	}
	public Long getTotalTimeTaken() {
		return totalTimeTaken;
	}
	public List<SectionResultDto> getSectionVsSectionRes() {
		return sectionVsSectionRes;
	}
	
}
