package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.dto.BlogCategoryDTO;
import com.mongodb.DBObject;

public interface BlogCategoryService {

	BlogCategoryDTO saveBlogCategory(BlogCategoryDTO payload) throws Exception;

	List<BlogCategoryDTO> saveBlogCategoryList(List<BlogCategoryDTO> payloadList) throws Exception;

	List<BlogCategoryDTO> fetchBlogCategory(int page, int size) throws Exception;

	List<DBObject> fetchCountCategoryWise() throws Exception;

}
