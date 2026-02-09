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

import com.book.ensureu.dto.BlogCategoryDTO;
import com.book.ensureu.service.BlogCategoryService;
import com.mongodb.DBObject;

@RestController
@RequestMapping("/blog/category")
public class BlogCategoryApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BlogCategoryApi.class);

	@Autowired
	BlogCategoryService blogCategoryService;

	@CrossOrigin
	@PostMapping("/add")
	public ResponseEntity<?> addCategory(@RequestBody BlogCategoryDTO payload) {
		ResponseEntity<?> res = null;
		try {
			BlogCategoryDTO obj = blogCategoryService.saveBlogCategory(payload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in addCategory ", ex);
		}
		return res;

	}

	@CrossOrigin
	@PostMapping("/add/list")
	public ResponseEntity<?> addCategoryList(@RequestBody List<BlogCategoryDTO> payloadList) {
		ResponseEntity<?> res = null;
		try {
			List<BlogCategoryDTO> obj = blogCategoryService.saveBlogCategoryList(payloadList);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in addCategoryList ", ex);
		}
		return res;

	}

	@CrossOrigin
	@GetMapping("/fetch/all")
	public ResponseEntity<?> fetchCategory(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size) {
		ResponseEntity<?> res = null;
		try {
			List<BlogCategoryDTO> obj = blogCategoryService.fetchBlogCategory(page!=null?page:0, size!=null?size:0);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchCategory ", ex);
		}
		return res;

	}
	
	@CrossOrigin
	@GetMapping("/fetch/categories_count")
	public ResponseEntity<?> fetchCountCategoryWise() {
		ResponseEntity<?> res = null;
		try {
			List<DBObject> obj = blogCategoryService.fetchCountCategoryWise();
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchCountCategoryWise ", ex);
		}
		return res;

	}

}
