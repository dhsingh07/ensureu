package com.book.ensureu.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.UserLoginType;

@Document(collection="otpToken")
public class OtpToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5373738984756544643L;
	@Id
	private String otpToken;
	private String userId;
	@Indexed(name = "expire_after_seconds_index", expireAfterSeconds = 600)
	private LocalDateTime expiryOtpTokenTime;
	private UserLoginType loginType;

	public OtpToken() {
		super();
	}

	public OtpToken(String otpToken, String userId, LocalDateTime expiryOtpTokenTime, UserLoginType loginType) {
		super();
		this.otpToken = otpToken;
		this.userId = userId;
		this.expiryOtpTokenTime = expiryOtpTokenTime;
		this.loginType = loginType;
	}

	public String getOtpToken() {
		return otpToken;
	}

	public String getUserId() {
		return userId;
	}

	public LocalDateTime getExpiryOtpTokenTime() {
		return expiryOtpTokenTime;
	}

	public UserLoginType getLoginType() {
		return loginType;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((otpToken == null) ? 0 : otpToken.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		OtpToken other = (OtpToken) obj;
		if (otpToken == null) {
			if (other.otpToken != null)
				return false;
		} else if (!otpToken.equals(other.otpToken))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OtpToken [otpToken=" + otpToken + ", userId=" + userId + ", expiryOtpTokenTime=" + expiryOtpTokenTime
				+ ", loginType=" + loginType + "]";
	}
	
	

}
