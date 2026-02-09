package com.book.ensureu.dto;

import java.io.Serializable;
import java.util.List;

import com.book.ensureu.constant.UserLoginType;
import com.book.ensureu.model.Address;
import com.book.ensureu.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class UserDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7120734676784529920L;

	private Long id;
	private String userName;
	private String password;
	private String emailId;
	private String firstName;
	private String lastName;
	private String dob;
	private String mobileNumber;
	private boolean mobileNumberVeriffied;
	private Address address;
	private UserLoginType userLoginType;
	private List<Role> roles;
	private String gender;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public boolean isMobileNumberVeriffied() {
		return mobileNumberVeriffied;
	}
	public void setMobileNumberVeriffied(boolean mobileNumberVeriffied) {
		this.mobileNumberVeriffied = mobileNumberVeriffied;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public UserLoginType getUserLoginType() {
		return userLoginType;
	}
	public void setUserLoginType(UserLoginType userLoginType) {
		this.userLoginType = userLoginType;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", userName=" + userName + ", password=" + password + ", emailId=" + emailId
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", mobileNumber="
				+ mobileNumber + ", mobileNumberVeriffied=" + mobileNumberVeriffied + ", address=" + address
				+ ", userLoginType=" + userLoginType + ", roles=" + roles + ", gender=" + gender + "]";
	}
	
	
	
}
