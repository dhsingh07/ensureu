package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.BlogTagsDTO;
import com.book.ensureu.service.BlogTagsService;

@RestController
@RequestMapping("/blog/tags")
public class BlogTagsApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BlogTagsApi.class);

	@Autowired
	BlogTagsService blogTagsService;

	@CrossOrigin
	@PostMapping("/add")
	public ResponseEntity<?> addTags(@RequestBody BlogTagsDTO payload) {
		ResponseEntity<?> res = null;
		try {
			BlogTagsDTO obj = blogTagsService.saveBlogTags(payload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in addTags ", ex);
		}
		return res;

	}

	@CrossOrigin
	@PostMapping("/add/list")
	public ResponseEntity<?> addTagsList(@RequestBody List<BlogTagsDTO> payloadList) {
		ResponseEntity<?> res = null;
		try {
			List<BlogTagsDTO> obj = blogTagsService.saveBlogTagsList(payloadList);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in addTagsList ", ex);
		}
		return res;

	}

	@CrossOrigin
	@GetMapping("/fetch/all")
	public ResponseEntity<?> fetchTags(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size) {
		ResponseEntity<?> res = null;
		try {
			List<BlogTagsDTO> obj = blogTagsService.fetchBlogTags(page!=null?page:0, size!=null?size:0);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchTags ", ex);
		}
		return res;

	}

}
