package com.book.ensureu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.PaperType;

@Document(collection ="userPaperEnrollment")
public class UserPaperEnrollment {

	@Id
	private Long id;
	private String userId;
	private PaperType paperType;
	private String enrollment;
	private Long testPaperId;
	private int days;
	private Long subscriptionStartDate;
	private Long subscriptionEndDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public PaperType getPaperType() {
		return paperType;
	}
	public void setPaperType(PaperType paperType) {
		this.paperType = paperType;
	}
	public String getEnrollment() {
		return enrollment;
	}
	public void setEnrollment(String enrollment) {
		this.enrollment = enrollment;
	}
	public Long getTestPaperId() {
		return testPaperId;
	}
	public void setTestPaperId(Long testPaperId) {
		this.testPaperId = testPaperId;
	}
	
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public Long getSubscriptionStartDate() {
		return subscriptionStartDate;
	}
	public void setSubscriptionStartDate(Long subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}
	public Long getSubscriptionEndDate() {
		return subscriptionEndDate;
	}
	public void setSubscriptionEndDate(Long subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}
	@Override
	public String toString() {
		return "UserPaperEnrollment [id=" + id + ", userId=" + userId + ", paperType=" + paperType + ", enrollment="
				+ enrollment + ", testPaperId=" + testPaperId + "]";
	}
	
}
