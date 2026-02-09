package com.book.ensureu.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author dharmendra.singh
 *
 */
public class JwtUser implements UserDetails,Serializable{


	    /**
	 * 
	 */
	private static final long serialVersionUID = -670827767714461051L;
		private final Long id;
	    private final String username;
	    private final String firstname;
	    private final String lastname;
	    private final String password;
	    private final String email;
	    private final Collection<? extends GrantedAuthority> authorities;
	    private final boolean enabled;
	    private final Date lastPasswordResetDate;
	    private boolean verificationFlag;
	    private List<Role> roles;
	    
		public JwtUser(Long id, String username, String firstname, String lastname, String password, String email,
				Collection<? extends GrantedAuthority> authorities, boolean enabled, Date lastPasswordResetDate) {
			super();
			this.id = id;
			this.username = username;
			this.firstname = firstname;
			this.lastname = lastname;
			this.password = password;
			this.email = email;
			this.authorities = authorities;
			this.enabled = enabled;
			this.lastPasswordResetDate = lastPasswordResetDate;
		}
		
		public JwtUser(Long id, String username, String firstname, String lastname, String password, String email,
				Collection<? extends GrantedAuthority> authorities, boolean enabled, Date lastPasswordResetDate,boolean verificationFlag) {
			super();
			this.id = id;
			this.username = username;
			this.firstname = firstname;
			this.lastname = lastname;
			this.password = password;
			this.email = email;
			this.authorities = authorities;
			this.enabled = enabled;
			this.lastPasswordResetDate = lastPasswordResetDate;
			this.verificationFlag=verificationFlag;
		}

		
		public JwtUser(Long id, String username, String firstname, String lastname, String password, String email,
				Collection<? extends GrantedAuthority> authorities, boolean enabled, Date lastPasswordResetDate,
				boolean verificationFlag, List<Role> roles) {
			super();
			this.id = id;
			this.username = username;
			this.firstname = firstname;
			this.lastname = lastname;
			this.password = password;
			this.email = email;
			this.authorities = authorities;
			this.enabled = enabled;
			this.lastPasswordResetDate = lastPasswordResetDate;
			this.verificationFlag = verificationFlag;
			this.roles = roles;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		public Long getId() {
			return id;
		}

		public String getEmail() {
			return email;
		}

		public Date getLastPasswordResetDate() {
			return lastPasswordResetDate;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		public String getFirstname() {
			return firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public boolean isVerificationFlag() {
			return verificationFlag;
		}

		public void setVerificationFlag(boolean verificationFlag) {
			this.verificationFlag = verificationFlag;
		}
		
		public List<Role> getRoles() {
			return roles;
		}

		@Override
		public String toString() {
			return "JwtUser [id=" + id + ", username=" + username + ", firstname=" + firstname + ", lastname="
					+ lastname + ", password=" + password + ", email=" + email + ", authorities=" + authorities
					+ ", enabled=" + enabled + ", lastPasswordResetDate=" + lastPasswordResetDate
					+ ", verificationFlag=" + verificationFlag + ", roles=" + roles + "]";
		}
	   
		
}
