package com.book.ensureu.util;

import java.io.File;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaperImageUploadHelper {
	
	public static String imageUplaod(GoogleCloudStorageUtil gcpUtil,String bucketName,MultipartFile file,String filePath) throws Exception {
		String url=null;
		try {
			 url=gcpUtil.uploadObjectInBucket(bucketName, file, filePath, file.getContentType());
		} catch (Exception e) {
			throw e;
		}
		return url;
	}
	
	public static String imageUplaod(GoogleCloudStorageUtil gcpUtil,String bucketName,File file,String filePath,String contentType) throws Exception {
		String url=null;
		try {
			 url=gcpUtil.uploadObjectInBucket(bucketName, (MultipartFile) file, filePath, contentType);
		} catch (Exception e) {
			throw e;
		}
		return url;
	}

}
