package com.book.ensureu.service.provider.impl;

import java.io.IOException;
import java.io.InputStream;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Qualifier("facebookProviderTokenService")
public class FacebookProviderTokenValidateImpl implements ProviderTokenValidate {

	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookProviderTokenValidateImpl.class.getName());

	@Value("${facebook.token.validate.url}")
	private String facebookOAuthValidateUrl;

	@Autowired
	private WebClient httpWebClient;

	@Override
	public boolean validateToken(String userId, String token) throws IOException, WebClientException {

		LOGGER.info("facebook userId : " + userId);
		if (token != null && userId != null) {
			return validateToken(token);
		}
		return false;
	}

	@Override
	public boolean validateToken(String token) throws IOException, WebClientException {

		/*if (token != null) {
			Map<String, Object> tokenMap = null;

			try {
				String path = facebookOAuthValidateUrl + "=" + token;
				Map<String, Object> headers = new HashMap<>();
				headers.put("Content-Type", "application/json");
				byte[] inputStream = httpWebClient.getRaw(path, headers);
				ObjectMapper objectMapper = new ObjectMapper();
				tokenMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
				});

			} catch (WebClientException ex) {
				LOGGER.error("Exception" + ex.getMessage());
				return false;
			}
			LOGGER.info("facebook name : " + tokenMap.get("name"));
			if (tokenMap.containsKey("name")) {
				return true;
			}
		}*/
		return false;
	}

}
