package com.book.ensureu.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.UserLoginType;

@Document(collection="oauthToken")
public class ProviderOauthToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 839364878868041476L;

	@Id
	private String token;
	private String username;
	private String name; // User's display name from provider
	@Indexed(name = "expire_after_seconds_index", expireAfterSeconds = 3600)
	private LocalDateTime expiryTokenTime;
	private UserLoginType loginType;
	private LocalDateTime createDateTime;
    private String ipAddress;
    private String city;
    private String region;
    private String country;
	
	
	public ProviderOauthToken() {
		super();
	}

	public ProviderOauthToken(String token, String username, UserLoginType loginType) {
		super();
		this.token = token;
		this.username = username;
		this.expiryTokenTime=LocalDateTime.now();
		this.loginType=loginType;
	}

	
	public ProviderOauthToken(String token, String username, LocalDateTime expiryTokenTime, UserLoginType loginType,
			LocalDateTime createDateTime) {
		super();
		this.token = token;
		this.username = username;
		this.expiryTokenTime = expiryTokenTime;
		this.loginType = loginType;
		this.createDateTime = createDateTime;
	}
	
	
	public ProviderOauthToken(String token, String username, LocalDateTime expiryTokenTime, UserLoginType loginType,
			LocalDateTime createDateTime, String ipAddress, String city, String region, String country) {
		super();
		this.token = token;
		this.username = username;
		this.expiryTokenTime = expiryTokenTime;
		this.loginType = loginType;
		this.createDateTime = createDateTime;
		this.ipAddress = ipAddress;
		this.city = city;
		this.region = region;
		this.country = country;
	}

	public String getToken() {
		return token;
	}


	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getExpiryTokenTime() {
		return expiryTokenTime;
	
	}

	public void setExpiryTokenTime(LocalDateTime expiryTokenTime) {
		this.expiryTokenTime = expiryTokenTime;
	}

	public UserLoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(UserLoginType loginType) {
		this.loginType = loginType;
	}

	
	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	
	public String getIpAddress() {
		return ipAddress;
	}

	public String getCity() {
		return city;
	}

	public String getRegion() {
		return region;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public String toString() {
		return "ProviderOauthTokenRequest [token=" + token + ", username=" + username + ", expiryTokenTime="
				+ expiryTokenTime + ", loginType=" + loginType + "]";
	}
	
	
}
