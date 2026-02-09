package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="userCourcesEnrollment")
public class UserCourcesEnrollment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4771052094108233113L;
	private String userId;
	private String emailId;
	private List<CoursesModel> cources;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public List<CoursesModel> getCources() {
		return cources;
	}
	public void setCources(List<CoursesModel> cources) {
		this.cources = cources;
	}
	@Override
	public String toString() {
		return "UserCourcesEnrollment [userId=" + userId + ", emailId=" + emailId + ", cources=" + cources + "]";
	}
	
	
	
}
