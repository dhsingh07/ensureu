package com.book.ensureu.service.provider.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.book.ensureu.service.provider.ProviderTokenValidate;
import com.book.ensureu.web.WebClient;
import com.book.ensureu.web.WebClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;



@Service
@Qualifier("googleProviderTokenService")
public class GoogleProviderTokenValidateImpl implements ProviderTokenValidate {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleProviderTokenValidateImpl.class.getName());

	@Value("${google.token.validate.url}")
	private String googleOAuthValidateUrl;

	@Value("${google.client.id}")
	private String googleClientId;

	@Autowired
	private WebClient httpWebClient;

	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	@Override
	public boolean validateToken(String userId, String token) throws IOException, WebClientException {

		LOGGER.info("google userId : " + userId);
		if (token != null && userId != null) {
		//	return validateToken(token);
			return validateIdToken(token);
		}
		return false;
	}

	@Override
	public boolean validateToken(String token) throws IOException, WebClientException {

		Map<String, Object> tokenMap = null;
		if (token != null) {
			String path = googleOAuthValidateUrl + "=" + token;
			// String path = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token="
			// + token;
			try {
				Map<String, Object> headers = new HashMap<>();
				headers.put("Content-Type", "application/json");
				byte[] inputStream = httpWebClient.getRaw(path, headers);
				ObjectMapper objectMapper = new ObjectMapper();
				/*JsonNode jsonNode = objectMapper.readTree(inputStream);
				String email=jsonNode.get("email").asText();
				 if(email!=null && !email.isEmpty()) { return true; }*/
				tokenMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
				});

			} catch (WebClientException ex) {
				LOGGER.error("Exception" + ex.getMessage());
				return false;
			}
			System.out.println("google email : " + tokenMap.get("email"));
			LOGGER.info("google email : " + tokenMap.get("email"));
			if (tokenMap.containsKey("email")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Validates a Google ID token locally without making an API call.
	 * This is the recommended approach for validating Google tokens as it:
	 * - Does not require network calls to Google's servers
	 * - Validates signature using Google's public keys
	 * - Validates token expiration and audience
	 *
	 * @param idTokenString The Google ID token (JWT) to validate
	 * @return true if the token is valid, false otherwise
	 */
	public boolean validateIdToken(String idTokenString) {
		if (idTokenString == null || idTokenString.isEmpty()) {
			LOGGER.warn("ID token is null or empty");
			return false;
		}

		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY)
					.setAudience(Collections.singletonList(googleClientId))
					.build();

			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken == null) {
				LOGGER.warn("Invalid Google ID Token - verification failed");
				return false;
			}

			Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			boolean emailVerified = payload.getEmailVerified();
			String userId = payload.getSubject();

			LOGGER.info("Google ID token validated successfully - Email: {}, User ID: {}, Email Verified: {}",
					email, userId, emailVerified);

			return emailVerified;
		} catch (GeneralSecurityException e) {
			LOGGER.error("Security exception while validating ID token: {}", e.getMessage(), e);
			return false;
		} catch (IOException e) {
			LOGGER.error("IO exception while validating ID token: {}", e.getMessage(), e);
			return false;
		} catch (Exception e) {
			LOGGER.error("Unexpected exception while validating ID token: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Validates a Google ID token and extracts user information.
	 *
	 * @param idTokenString The Google ID token (JWT) to validate
	 * @return Map containing user information (email, userId, name, picture) if valid, null otherwise
	 */
	public Map<String, String> validateIdTokenAndGetUserInfo(String idTokenString) {
		if (idTokenString == null || idTokenString.isEmpty()) {
			LOGGER.warn("ID token is null or empty");
			return null;
		}

		try {
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY)
					.setAudience(Collections.singletonList(googleClientId))
					.build();

			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken == null) {
				LOGGER.warn("Invalid Google ID Token - verification failed");
				return null;
			}

			Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			boolean emailVerified = payload.getEmailVerified();

			if (!emailVerified) {
				LOGGER.warn("Email not verified for ID token");
				return null;
			}

			Map<String, String> userInfo = new HashMap<>();
			userInfo.put("email", email);
			userInfo.put("userId", payload.getSubject());
			userInfo.put("name", (String) payload.get("name"));
			userInfo.put("givenName", (String) payload.get("given_name"));
			userInfo.put("familyName", (String) payload.get("family_name"));
			userInfo.put("picture", (String) payload.get("picture"));
			userInfo.put("emailVerified", String.valueOf(emailVerified));

			LOGGER.info("Google ID token validated and user info extracted - Email: {}, User ID: {}",
					email, payload.getSubject());

			return userInfo;
		} catch (GeneralSecurityException e) {
			LOGGER.error("Security exception while validating ID token: {}", e.getMessage(), e);
			return null;
		} catch (IOException e) {
			LOGGER.error("IO exception while validating ID token: {}", e.getMessage(), e);
			return null;
		} catch (Exception e) {
			LOGGER.error("Unexpected exception while validating ID token: {}", e.getMessage(), e);
			return null;
		}
	}

}
