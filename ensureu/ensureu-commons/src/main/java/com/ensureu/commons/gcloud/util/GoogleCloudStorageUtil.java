package com.ensureu.commons.gcloud.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.Storage.Objects.Delete;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;

@Component
public class GoogleCloudStorageUtil {

	private static Storage storageService;
	private static boolean initialized = false;
	private static boolean available = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCloudStorageUtil.class.getName());

	static String APPLICATION_NAME = "EnsureU";
	static String RESOURCE_FILE_NAME = "credentials.json";

	// Lazy initialization - don't fail on startup if credentials not present
	private static synchronized void ensureInitialized() {
		if (initialized) {
			return;
		}
		initialized = true;
		try {
			storageService = buildStorage();
			available = (storageService != null);
			if (available) {
				LOGGER.info("Google Cloud Storage initialized successfully");
			}
		} catch (Exception e) {
			LOGGER.warn("Google Cloud Storage not available: {}. Image upload to GCS will be disabled.", e.getMessage());
			available = false;
		}
	}

	private static Storage buildStorage() {
		try {
			ClassPathResource classPathResource = new ClassPathResource(RESOURCE_FILE_NAME);

			// Check if credentials file exists
			if (!classPathResource.exists()) {
				LOGGER.warn("GCS credentials file '{}' not found in classpath. GCS features disabled.", RESOURCE_FILE_NAME);
				return null;
			}

			HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			LOGGER.info("Loading GCS credentials from: {}", classPathResource);

			InputStream inputStream = classPathResource.getInputStream();
			GoogleCredential credential = GoogleCredential.fromStream(inputStream, transport, jsonFactory);

			if (credential != null && credential.createScopedRequired()) {
				LOGGER.info("GCS credential loaded successfully");
				Collection<String> scopes = StorageScopes.all();
				credential = credential.createScoped(scopes);
			} else {
				LOGGER.error("Failed to create scoped GCS credential");
				return null;
			}

			return new Storage.Builder(transport, jsonFactory, credential)
					.setApplicationName(APPLICATION_NAME)
					.build();
		} catch (Exception ex) {
			LOGGER.error("Exception while building cloud storage service: {}", ex.getMessage());
			return null;
		}
	}

	public boolean isAvailable() {
		ensureInitialized();
		return available;
	}

	public void deleteObjectFromBucket(String bucketName, String filename) throws Exception {
		ensureInitialized();
		if (!available) {
			throw new UnsupportedOperationException("Google Cloud Storage is not configured");
		}
		Delete deleteRequest = storageService.objects().delete(bucketName, filename);
		deleteRequest.execute();
	}

	public String uploadObjectInBucket(String bucketName, MultipartFile file, String filename, String contentType)
			throws Exception {
		ensureInitialized();
		if (!available) {
			LOGGER.warn("GCS not available, cannot upload file: {}", filename);
			return null;
		}
		InputStreamContent contentStream = new InputStreamContent(contentType, file.getInputStream());
		contentStream.setLength(file.getBytes().length);
		StorageObject objectMetadata = new StorageObject()
				// Set the destination object name
				.setName(filename)
				// Set the access control list to publicly read-only
				.setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));
		Storage.Objects.Insert insertRequest = storageService.objects().insert(bucketName, objectMetadata,
				contentStream);
		StorageObject response = insertRequest.execute();
		return response != null ? response.getMediaLink() : null;
	}

}
