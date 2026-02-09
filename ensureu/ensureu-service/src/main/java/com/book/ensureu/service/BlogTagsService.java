package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.dto.BlogTagsDTO;

public interface BlogTagsService {

	BlogTagsDTO saveBlogTags(BlogTagsDTO payload) throws Exception;

	List<BlogTagsDTO> saveBlogTagsList(List<BlogTagsDTO> payloadList) throws Exception;

	List<BlogTagsDTO> fetchBlogTags(int page, int size) throws Exception;

}
