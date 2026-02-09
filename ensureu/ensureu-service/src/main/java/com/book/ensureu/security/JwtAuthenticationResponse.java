package com.book.ensureu.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.book.ensureu.model.Role;
import com.book.ensureu.model.UserTenant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class JwtAuthenticationResponse implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 2383909929160660193L;
	private final String token;
	private String username;
	private String name;
	private boolean verification;
	private UserTenant userTenant;
    private List<Role> roles;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    
    public JwtAuthenticationResponse(String token, String username, String name) {
		super();
		this.token = token;
		this.username = username;
		this.name = name;
	}
    
    


	public JwtAuthenticationResponse(String token, String username, String name, boolean verification) {
		super();
		this.token = token;
		this.username = username;
		this.name = name;
		this.verification = verification;
	}


	
	public JwtAuthenticationResponse(String token, String username, String name, boolean verification,
			List<Role> roles) {
		super();
		this.token = token;
		this.username = username;
		this.name = name;
		this.verification = verification;
		this.roles = roles;
	}


	public JwtAuthenticationResponse(String token, String username, String name, boolean verification,
			UserTenant userTenant, List<Role> roles) {
		super();
		this.token = token;
		this.username = username;
		this.name = name;
		this.verification = verification;
		this.userTenant = userTenant;
		this.roles =roles;
	}


	public boolean isVerification() {
		return verification;
	}


	public void setVerification(boolean verification) {
		this.verification = verification;
	}


	public String getToken() {
        return this.token;
    }


	public String getUsername() {
		return username;
	}


	public String getName() {
		return name;
	}


	public UserTenant getUserTenant() {
		return userTenant;
	}


	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationResponse [token=" + token + ", username=" + username + ", name=" + name
				+ ", verification=" + verification + ", userTenant=" + userTenant + ", roles=" + roles + "]";
	}
	
}
