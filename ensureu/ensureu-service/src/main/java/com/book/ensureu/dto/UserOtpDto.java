package com.book.ensureu.dto;

public class UserOtpDto {
	private String userName;
	private String otp;
	private String password;
	private String confirmPassword;

	
	public UserOtpDto() {
		super();
	}

	public UserOtpDto(String userName) {
		super();
		this.userName = userName;
	}

	public UserOtpDto(String userName, String otp) {
		super();
		this.userName = userName;
		this.otp = otp;
	}

	public UserOtpDto(String userName, String otp, String password, String confirmPassword) {
		super();
		this.userName = userName;
		this.otp = otp;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getUserName() {
		return userName;
	}

	public String getOtp() {
		return otp;
	}

	public String getPassword() {
		return password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	
}
