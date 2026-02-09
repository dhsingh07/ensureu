package com.book.ensureu.security;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class  JwtAuthenticationRequest implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -5777201676972214041L;
	private String username;
    private String password;
    private String ipAddress;
    private String city;
    private String region;
    private String country;
    private Long createDate;
    private Long modifiedDate;

    public JwtAuthenticationRequest() {
        super();
    }

    public JwtAuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    
    public JwtAuthenticationRequest(String username, String password, String ipAddress) {
		super();
		this.username = username;
		this.password = password;
		ipAddress = ipAddress;
	}

    
	public JwtAuthenticationRequest(String username, String password, String ipAddress, String city, String region,
			String country) {
		super();
		this.username = username;
		this.password = password;
		ipAddress = ipAddress;
		this.city = city;
		this.region = region;
		this.country = country;
	}

	public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		ipAddress = ipAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationRequest [username=" + username + ", IpAddress=" + ipAddress
				+ ", city=" + city + ", region=" + region + ", country=" + country + "]";
	}
    
    
}
