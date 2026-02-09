package com.book.ensureu.configuration;

import com.book.ensureu.util.EncryptDecryptUtil;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Configuration
public class MongoDataSourceConfiguration {

	@Bean
	public MongoTemplate mongoTemplate(Environment env) {
		try {
			// Check if a direct URI is provided
			String directUri = env.getProperty("simple.data.mongodb.uri");
			if (directUri != null && !directUri.isEmpty()) {
				String dbName = extractDbName(directUri);
				log.info("Connecting to MongoDB using URI, database: '{}'", dbName);
				return new MongoTemplate(MongoClients.create(directUri), dbName);
			}

			// Fall back to individual properties with encrypted password
			String host = env.getProperty("simple.data.mongodb.host", "localhost");
			String port = env.getProperty("simple.data.mongodb.port", "27017");
			String dbName = env.getProperty("simple.data.mongodb.name", "ensureu");
			String username = env.getProperty("simple.data.mongodb.username");
			String encryptedPassword = env.getProperty("simple.data.mongodb.password");
			String salt = env.getProperty("spring.encryption.salt");

			// Decrypt password
			String decryptedPassword = EncryptDecryptUtil.jasyptDecryptedPropertyValue(encryptedPassword, salt);

			// Escape special characters
			String encodedPassword = URLEncoder.encode(decryptedPassword, "UTF-8");

			// Construct Mongo URI
			String uri = String.format("mongodb://%s:%s@%s:%s/%s?authSource=%s",
					username, encodedPassword, host, port, dbName, dbName);

			log.info("Connecting to MongoDB at {} with user '{}'", host, username);

			// Create MongoTemplate
			return new MongoTemplate(MongoClients.create(uri), dbName);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error encoding MongoDB password", e);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing MongoTemplate", e);
		}
	}

	private String extractDbName(String uri) {
		// Extract database name from URI: mongodb://user:pass@host:port/dbName?params
		String withoutPrefix = uri.replaceFirst("mongodb://[^/]*/", "");
		int queryIndex = withoutPrefix.indexOf('?');
		return queryIndex > 0 ? withoutPrefix.substring(0, queryIndex) : withoutPrefix;
	}
}
