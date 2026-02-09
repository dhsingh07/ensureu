package com.book.ensureu.service;

import javax.naming.AuthenticationException;

import org.springframework.http.ResponseEntity;

import com.book.ensureu.model.ProviderOauthToken;

public interface AutenticationService {

	public void authenticate(String userName,String password) throws AuthenticationException;
	public ResponseEntity<?> generateTokenforUser(String userName,String password) throws AuthenticationException;
	public ResponseEntity<?> saveProviderOauth(ProviderOauthToken providerOauthTokenRequest) throws AuthenticationException;
	
	public ProviderOauthToken getProviderOauthByToken(String providerToken);
	public ResponseEntity<?> refressTokenforUser(String token) throws AuthenticationException;
	
}
