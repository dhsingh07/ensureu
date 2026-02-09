package com.book.ensureu.api;

import java.io.IOException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.annotation.UserAuditLogin;
import com.book.ensureu.model.ProviderOauthToken;
import com.book.ensureu.security.JwtAuthenticationRequest;
import com.book.ensureu.security.util.JwtSecurityTokenUtil;
import com.book.ensureu.service.AutenticationService;
import com.book.ensureu.service.impl.JwtUserDetailsService;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationApi {

	@Autowired
	AutenticationService authenticationService;
	
	@Autowired
	JwtSecurityTokenUtil jwtSecurityTokenUtil;
	
	@Autowired
	JwtUserDetailsService jwtUserDetailsService;

	@CrossOrigin
	@UserAuditLogin
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> generateAuthToken(@RequestBody final JwtAuthenticationRequest jwtAuthenticationRequest)
			throws AuthenticationException {
		return authenticationService.generateTokenforUser(jwtAuthenticationRequest.getUsername(),
				jwtAuthenticationRequest.getPassword());
	}
	
	@CrossOrigin
	@RequestMapping(value = "v1/token", method = RequestMethod.POST)
	public ResponseEntity<?> generateAuthTokenV1(@RequestBody final JwtAuthenticationRequest jwtAuthenticationRequest)
			throws AuthenticationException {
		return authenticationService.generateTokenforUser(jwtAuthenticationRequest.getUsername(),
				jwtAuthenticationRequest.getPassword());
	}

	@UserAuditLogin
	@CrossOrigin
	@RequestMapping(value = "/providertoken", method = RequestMethod.POST)
	public ResponseEntity<?> saveAuthToken(@RequestBody final ProviderOauthToken providerOauthTokenRequest)
			throws AuthenticationException {
		return authenticationService.saveProviderOauth(providerOauthTokenRequest);
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request,HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		String authToken = request.getHeader("Authorization");
		final String token = authToken.substring(7);
		return authenticationService.refressTokenforUser(token);

	}

}
