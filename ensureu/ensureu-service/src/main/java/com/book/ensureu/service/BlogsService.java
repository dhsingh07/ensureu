package com.book.ensureu.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.book.ensureu.dto.BlogCommentsDTO;
import com.book.ensureu.dto.BlogsDTO;

public interface BlogsService {

	BlogsDTO saveBlog(BlogsDTO blogsPayload) throws Exception;

	List<BlogsDTO> fetchAll(int size, int page, String sortBy, String direction) throws Exception;

	BlogCommentsDTO saveBlogComments(BlogCommentsDTO blogCommentsPayload) throws Exception;

	BlogCommentsDTO fetchCommentsById(String blogId) throws Exception;

	List<BlogsDTO> fetchByCategory(int size, int page, String sortBy, String categoryId) throws Exception;

	void deleteBlog(String blogId) throws Exception;

	ResponseEntity<?> uploadImage(MultipartFile file) throws Exception;

	List<BlogsDTO> searchByTitle(String phrase) throws Exception;

	List<BlogsDTO> fetchByUser(int size, int page, String sortBy, String userId) throws Exception;

	BlogsDTO partialUpdateBlog(BlogsDTO blogsPayload,boolean isDislike) throws Exception;
	
	BlogsDTO fetchById(String blogId) throws Exception;

}
