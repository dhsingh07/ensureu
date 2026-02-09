package com.book.ensureu.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.UserLoginType;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.model.ProviderOauthToken;
import com.book.ensureu.model.User;
import com.book.ensureu.security.JwtUserFactory;
import com.book.ensureu.repository.AutenticationProviderRepository;
import com.book.ensureu.security.JwtAuthenticationResponse;
import com.book.ensureu.security.util.JwtSecurityTokenUtil;
import com.book.ensureu.service.AutenticationService;
import com.book.ensureu.service.UserService;
import com.book.ensureu.service.provider.ProviderTokenValidate;
import com.book.ensureu.web.WebClientException;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class AutenticationServiceImpl implements AutenticationService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AutenticationServiceImpl.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	JwtSecurityTokenUtil jwtSecurityTokenUtil;

	@Autowired
	JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	UserService userService;

	@Autowired
	AutenticationProviderRepository autenticationProviderRepository;

	@Autowired
	@Qualifier("googleProviderTokenService")
	ProviderTokenValidate googleProviderTokenService;

	@Autowired
	@Qualifier("facebookProviderTokenService")
	ProviderTokenValidate facebookProviderTokenService;

	@Override
	public void authenticate(String userName, String password) throws AuthenticationException {
		Objects.requireNonNull(userName);
		Objects.requireNonNull(password);
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		} catch (DisabledException dis) {
			throw new AuthenticationException("Disbaled user" + dis);
		} catch (BadCredentialsException bad) {
			throw new AuthenticationException("bad credencial" + bad);
		}

	}

	@Override
	public ResponseEntity<?> generateTokenforUser(String userName, String password) throws AuthenticationException {
		LOGGER.info("Generate token {}",userName);
		authenticate(userName, password);
		UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userName);
		JwtUser user = (JwtUser) userDetails;
		if (!user.isVerificationFlag()) {
			LOGGER.info("User mobile number is not verified {}",user.isVerificationFlag());
			return ResponseEntity.ok(new JwtAuthenticationResponse(null, user.getUsername(),
					user.getFirstname() + " " + user.getLastname(), user.isVerificationFlag(),user.getRoles()));
		}
		String accessToken = jwtSecurityTokenUtil.generateToken(userDetails);
		ProviderOauthToken providerOauthTokenRequest = new ProviderOauthToken(accessToken, userName,
				UserLoginType.SIGNUP);
		autenticationProviderRepository.save(providerOauthTokenRequest);
		return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, user.getUsername(),
				user.getFirstname() + " " + user.getLastname(), user.isVerificationFlag(),user.getRoles()));
	}
	
	
	/**
	 * Generate JWT token for provider-authenticated user.
	 * This method does NOT call authenticate() - the provider has already validated the user.
	 * It directly creates JWT from the User entity.
	 *
	 * @param user The user entity (already validated by provider)
	 * @param loginType The provider type (GOOGLE, FACEBOOK)
	 * @return ResponseEntity with JWT token
	 */
	public ResponseEntity<?> generateTokenForProviderUser(User user, UserLoginType loginType) {
		LOGGER.info("Generating JWT for provider user: {}", user.getUserName());

		// Create JwtUser directly from User entity - no password authentication needed
		JwtUser jwtUser = JwtUserFactory.createJwtUser(user);

		// Generate JWT token
		String accessToken = jwtSecurityTokenUtil.generateToken(jwtUser);

		// Save token to provider repository
		ProviderOauthToken providerOauthTokenRequest = new ProviderOauthToken(accessToken, user.getUserName(), loginType);
		autenticationProviderRepository.save(providerOauthTokenRequest);

		return ResponseEntity.ok(new JwtAuthenticationResponse(
				accessToken,
				jwtUser.getUsername(),
				jwtUser.getFirstname() + " " + jwtUser.getLastname(),
				true, // Provider users are always verified
				jwtUser.getRoles()));
	}

	/*
	 * Validate provider token (Google/Facebook) and generate JWT.
	 *
	 * Flow:
	 * 1. Validate provider token with respective provider service
	 * 2. Find or create user in database
	 * 3. Generate JWT token (same format as direct login)
	 * 4. Return JWT for authentication
	 */
	@Override
	public ResponseEntity<?> saveProviderOauth(ProviderOauthToken providerOauthTokenRequest) throws AuthenticationException {
		boolean validToken = true;
		UserLoginType loginType = providerOauthTokenRequest.getLoginType();

		// Validate login type
		if (loginType == null) {
			throw new AuthenticationException("Login type is required");
		}

		// Validate provider token
		try {
			if (loginType == UserLoginType.GOOGLE) {
				validToken = googleProviderTokenService.validateToken(providerOauthTokenRequest.getUsername(),
						providerOauthTokenRequest.getToken());
			} else if (loginType == UserLoginType.FACEBOOK) {
				validToken = facebookProviderTokenService.validateToken(providerOauthTokenRequest.getUsername(),
						providerOauthTokenRequest.getToken());
			} else {
				validToken = false;
			}
		} catch (IOException e) {
			LOGGER.error("validate token ", e);
			validToken = false;
		} catch (WebClientException e) {
			LOGGER.error("validate token web ", e);
			validToken = false;
		}

		if (!validToken) {
			throw new AuthenticationException("token is not valid " + providerOauthTokenRequest.getToken());
		}

		// Save provider token info
		providerOauthTokenRequest.setExpiryTokenTime(LocalDateTime.now());
		autenticationProviderRepository.save(providerOauthTokenRequest);

		// Find or create user - this is the key change!
		// Instead of trying to authenticate with dummy password, we directly create/find user
		String displayName = providerOauthTokenRequest.getName();
		if (displayName == null || displayName.isEmpty()) {
			displayName = providerOauthTokenRequest.getUsername(); // Fallback to email if no name
		}
		User user = userService.findOrCreateProviderUser(
				providerOauthTokenRequest.getUsername(),
				displayName,
				loginType);

		// Generate JWT token directly from user entity (no password authentication)
		return generateTokenForProviderUser(user, loginType);
	}

	@Override
	public ProviderOauthToken getProviderOauthByToken(String providerToken) {

		try {
			Optional<ProviderOauthToken> providerOauth=autenticationProviderRepository.findById(providerToken);
			if(providerOauth.isPresent()) {
				return providerOauth.get();
			}
		} catch (NoSuchElementException e) {
			LOGGER.debug("records not found {}", e.getMessage());
		}
		return null;
	}

	 private boolean canUserTokenRefress(String token) {
		if(token!=null && !token.isEmpty()) {
			Optional<ProviderOauthToken> providerToken=autenticationProviderRepository.findById(token);
			if(providerToken.isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ResponseEntity<?> refressTokenforUser(String token) throws AuthenticationException {
		String username = jwtSecurityTokenUtil.getUsernameFromToken(token);
		JwtUser user = (JwtUser) jwtUserDetailsService.loadUserByUsername(username);
		if (canUserTokenRefress(token)) {
			String refreshedToken = jwtSecurityTokenUtil.refreshToken(token);
			ProviderOauthToken providerOauthTokenRequest=new ProviderOauthToken(refreshedToken,username,UserLoginType.SIGNUP);
			autenticationProviderRepository.save(providerOauthTokenRequest);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken, user.getUsername(),
					user.getFirstname() + " " + user.getLastname(),user.isVerificationFlag(),user.getRoles()));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

}
