package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.NotificationConfigDTO;
import com.book.ensureu.service.NotificationConfigService;

@RestController
@RequestMapping("/notification/config")
public class NotificationConfigApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationConfigApi.class);

	@Autowired
	NotificationConfigService notificationService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> save(@RequestBody NotificationConfigDTO payload) throws Exception {
		ResponseEntity<?> res = null;
		try {
			NotificationConfigDTO obj = notificationService.save(payload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in save ", ex);
		}
		return res;
	}

	@RequestMapping(value = "/fetch/subCategory", method = RequestMethod.GET)
	public ResponseEntity<?> findBySubCategory(@RequestParam String subCategory) throws Exception {
		ResponseEntity<?> res = null;
		try {
			NotificationConfigDTO obj = notificationService.findBySubCategory(subCategory);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in findBySubCategory ", ex);
		}
		return res;
	}

	@RequestMapping(value = "/fetch/all", method = RequestMethod.GET)
	public ResponseEntity<?> findAll() throws Exception {
		ResponseEntity<?> res = null;
		try {
			List<NotificationConfigDTO> objList = notificationService.findAll();
			res = objList != null ? ResponseEntity.ok(objList) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in findAll ", ex);
		}
		return res;
	}

}
