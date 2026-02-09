package com.book.ensureu.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.UserLoginType;

@Document(collection="user")
public class User extends BaseModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1807620944773636018L;
	
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
	private String password1;
	private String gender;
	private UserTenant userTenant;

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
	public Address getAddress() {
		return address;
	}

	public boolean isMobileNumberVeriffied() {
		return mobileNumberVeriffied;
	}

	public void setMobileNumberVeriffied(boolean mobileNumberVeriffied) {
		this.mobileNumberVeriffied = mobileNumberVeriffied;
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
	
	

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
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
		User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	public UserTenant getUserTenant() {
		return userTenant;
	}

	public void setUserTenant(UserTenant userTenant) {
		this.userTenant = userTenant;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", emailId=" + emailId
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", mobileNumber="
				+ mobileNumber + ", mobileNumberVeriffied=" + mobileNumberVeriffied + ", address=" + address
				+ ", userLoginType=" + userLoginType + ", roles=" + roles + ", password1=" + password1 + ", gender="
				+ gender + ", userTenant=" + userTenant + "]";
	}
	
	
	
}
