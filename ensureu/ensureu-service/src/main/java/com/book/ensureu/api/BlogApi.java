package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.dto.BlogCommentsDTO;
import com.book.ensureu.dto.BlogsDTO;
import com.book.ensureu.service.BlogsService;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/blog")
public class BlogApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BlogApi.class);

	@Autowired
	BlogsService blogsService;

	@CrossOrigin
	@PostMapping(value = "/create")
	public ResponseEntity<?> saveBlogs(@RequestBody BlogsDTO blogsPayload) {
		ResponseEntity<?> res = null;
		try {
			BlogsDTO obj = blogsService.saveBlog(blogsPayload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in saveBlogs ", ex);
		}
		return res;
	}

	@CrossOrigin
	@PutMapping(value = "/update")
	public ResponseEntity<?> updateBlogs(@RequestBody BlogsDTO blogsPayload) {
		ResponseEntity<?> res = null;
		try {
			if (blogsPayload != null && blogsPayload.getId() == null)
				return ResponseEntity.ok("Id must not be blank");
			BlogsDTO obj = blogsService.saveBlog(blogsPayload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in updateBlogs ", ex);
		}
		return res;
	}

	@CrossOrigin
	@PatchMapping(value = "/partial/update")
	public ResponseEntity<?> partialUpdateBlogs(@RequestBody BlogsDTO blogsPayload,@RequestParam(defaultValue = "false") String isDislike) {
		ResponseEntity<?> res = null;
		try {
			if (blogsPayload != null && blogsPayload.getId() == null)
				return ResponseEntity.ok("Id must not be blank");
			BlogsDTO obj = blogsService.partialUpdateBlog(blogsPayload,Boolean.parseBoolean(isDislike));
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in partialUpdateBlogs ", ex);
		}
		return res;
	}

	@CrossOrigin
	@GetMapping(value = "/searchByTitle")
	public ResponseEntity<?> searchBlogs(@RequestParam(value = "phrase") String phrase) {
		ResponseEntity<?> res = null;
		try {
			if (phrase == null)
				return ResponseEntity.ok("phrase must not be blank");
			List<BlogsDTO> obj = blogsService.searchByTitle(phrase);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in searchBlogs ", ex);
		}
		return res;
	}

	@CrossOrigin
	@DeleteMapping(value = "/delete/{blogId}")
	public ResponseEntity<?> deleteBlogs(@PathVariable(value = "blogId") String blogId) {
		ResponseEntity<?> res = null;
		try {
			if (blogId == null)
				return ResponseEntity.ok("Id must not be blank");
			blogsService.deleteBlog(blogId);
			res = ResponseEntity.ok().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in deleteBlogs ", ex);
		}
		return res;
	}
	
	@CrossOrigin
	@GetMapping(value = "/fetch/byId/{blogId}")
	public ResponseEntity<?> findBlogById(@PathVariable(value = "blogId") String blogId) {
		ResponseEntity<?> res = null;
		try {
			if (blogId == null)
				return ResponseEntity.ok("Id must not be blank");
			BlogsDTO obj = blogsService.fetchById(blogId);
			res = obj!=null ? ResponseEntity.ok(obj) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in findBlogById ", ex);
		}
		return res;
	}

	@CrossOrigin
	@GetMapping(value = "/fetch/all")
	public ResponseEntity<?> fetchBlogs(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(defaultValue = "createdDate") String sortBy,@RequestParam(defaultValue = "DESC") String direction) {
		ResponseEntity<?> res = null;
		try {
			List<BlogsDTO> objList = blogsService.fetchAll(size, page, sortBy, direction);
			res = objList != null ? ResponseEntity.ok(objList) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchBlogs ", ex);
		}
		return res;
	}

	@CrossOrigin
	@GetMapping(value = "/fetch/byCategory/{categoryId}")
	public ResponseEntity<?> fetchBlogsByCategory(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(defaultValue = "createdDate") String sortBy,
			@PathVariable(value = "categoryId") String categoryId) {
		ResponseEntity<?> res = null;
		try {
			List<BlogsDTO> objList = blogsService.fetchByCategory(size, page, sortBy, categoryId);
			res = objList != null ? ResponseEntity.ok(objList) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchBlogs byCategory ", ex);
		}
		return res;
	}

	@CrossOrigin
	@GetMapping(value = "/fetch/byUser")
	public ResponseEntity<?> fetchBlogsByUser(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam(defaultValue = "createdDate") String sortBy, @RequestParam(value = "userId") String userId) {
		ResponseEntity<?> res = null;
		try {
			List<BlogsDTO> objList = blogsService.fetchByUser(size, page, sortBy, userId);
			res = objList != null ? ResponseEntity.ok(objList) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchBlogs byUser ", ex);
		}
		return res;
	}

	@CrossOrigin
	@PostMapping(value = "/add/comments")
	public ResponseEntity<?> saveBlogComments(@RequestBody BlogCommentsDTO Payload) {
		ResponseEntity<?> res = null;
		try {
			BlogCommentsDTO obj = blogsService.saveBlogComments(Payload);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.badRequest().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in saveBlogComments ", ex);
		}
		return res;
	}

	@CrossOrigin
	@GetMapping(value = "/fetch/comments/{blogId}")
	public ResponseEntity<?> fetchBlogComments(@PathVariable("blogId") String blogId) {
		ResponseEntity<?> res = null;
		try {
			BlogCommentsDTO obj = blogsService.fetchCommentsById(blogId);
			res = obj != null ? ResponseEntity.ok(obj) : ResponseEntity.noContent().build();
		} catch (Exception ex) {
			res = ResponseEntity.badRequest().build();
			LOGGER.error("Exception in fetchBlogComments ", ex);
		}
		return res;
	}

	@CrossOrigin
	@PostMapping(value = "/upload/image")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		ResponseEntity<?> response = null;
		try {
			response = blogsService.uploadImage(file);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception in upload Image ", e);
			response = ResponseEntity.badRequest().body(null);
		}
		return response;
	}
}
