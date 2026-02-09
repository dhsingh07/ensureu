package com.book.ensureu.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.book.ensureu.model.JwtUser;
import com.book.ensureu.model.Role;
import com.book.ensureu.model.User;

public final class JwtUserFactory {

	private JwtUserFactory() {

	}

	public static JwtUser createJwtUser(User user) {
		return new JwtUser(user.getId(), user.getUserName(), user.getFirstName(), user.getLastName(),
				user.getPassword(), user.getEmailId(), mapToGrantedAuthoritiesToRoles(user.getRoles()), true,
				new Date(),user.isMobileNumberVeriffied(),user.getRoles());

	}

	private static List<GrantedAuthority> mapToGrantedAuthoritiesToRoles(List<Role> roles) {
		if(roles != null ) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().toString()))
				.collect(Collectors.toList());
	}
		return null;
		}
}
