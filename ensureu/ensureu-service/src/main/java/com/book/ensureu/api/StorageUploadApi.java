package com.book.ensureu.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.constant.ApplicationConstant;
import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;

/**
 * @author jatin.bansal
 *
 */
//@RestController
//@RequestMapping("/object")
public class StorageUploadApi {

	@Autowired
	GoogleCloudStorageUtil gcpUtil;

	@CrossOrigin
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		ResponseEntity<?> response = null;
		try {
			String url = gcpUtil.uploadObjectInBucket(ApplicationConstant.BUCKET_NAME, file, file.getOriginalFilename(),
					file.getContentType());
			if (url != null) {
				Map<String, String> urlMap = new HashMap<String, String>();
				urlMap.put("url", url);
				response = ResponseEntity.ok(urlMap);
			} else {
				response = ResponseEntity.badRequest().body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = ResponseEntity.badRequest().body(null);
		}
		return response;
	}

	@CrossOrigin
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestParam("filename") String filename) {
		ResponseEntity<?> response = null;
		try {
			gcpUtil.deleteObjectFromBucket("ensureu-content", filename);
			response = ResponseEntity.ok().build();
		} catch (Exception e) {
			response = ResponseEntity.badRequest().body(null);
		}
		return response;
	}

}
