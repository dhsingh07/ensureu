package com.book.ensureu.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.book.ensureu.model.JwtUser;

@Component
public class UserPrincipalService {

	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	public JwtUser getCurrentUserDetails() {
		
		if(getAuthentication().getPrincipal() instanceof JwtUser) {
			return (JwtUser)getAuthentication().getPrincipal();
		}
		return null;
	}
	
}
