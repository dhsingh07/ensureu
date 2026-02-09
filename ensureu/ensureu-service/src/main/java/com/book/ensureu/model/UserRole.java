package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author dharmendra.singh
 *
 */
@Document(collection = "userRole")
public class UserRole implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6591099798803532514L;
	@Id
	private String id;
	private String userId;
	private String userName;
	private List<Role> role;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<Role> getRole() {
		return role;
	}
	public void setRole(List<Role> role) {
		this.role = role;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public String toString() {
		return "UserRole [id=" + id + ", userId=" + userId + ", role=" + role + "]";
	}
	
	
	

}
