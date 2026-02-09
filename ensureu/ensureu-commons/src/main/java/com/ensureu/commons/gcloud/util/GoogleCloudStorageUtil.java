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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCloudStorageUtil.class.getName());

	static String APPLICATION_NAME = "EnsureU";
	static String RESOURCE_FILE_NAME = "credentials.json";

	static {
		if(storageService==null)
			storageService = buildStorage();
	}

	private static Storage buildStorage() {
		try {
			HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory jsonFactory = new JacksonFactory();
			ClassPathResource classPathResource=new ClassPathResource(RESOURCE_FILE_NAME);
			LOGGER.info("classPathResource "+classPathResource);
			GoogleCredential credential =null;
			if(classPathResource!=null) {
			InputStream inputStream=classPathResource.getInputStream();
			credential = // GoogleCredential.getApplicationDefault(transport, jsonFactory);
					GoogleCredential.fromStream(
							inputStream, transport,
							jsonFactory); // for Local
		
			if (credential!=null && credential.createScopedRequired()) {
				LOGGER.info("credential "+credential);
				Collection<String> scopes = StorageScopes.all();
				credential = credential.createScoped(scopes);
			}else {
				LOGGER.error("google bucket connection failed");
			}
			}
			return new Storage.Builder(transport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build();
		} catch (Exception ex) {
			throw new RuntimeException("Exception while building cloud storage service", ex);
		}
	}
	

	public void deleteObjectFromBucket(String bucketName, String filename) throws Exception {
		Delete deleteRequest = storageService.objects().delete(bucketName, filename);
		deleteRequest.execute();
	}

	public String uploadObjectInBucket(String bucketName, MultipartFile file, String filename, String contentType)
			throws Exception {
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
