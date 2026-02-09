package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.dto.NotificationConfigDTO;

public interface NotificationConfigService {

	NotificationConfigDTO save(NotificationConfigDTO payload) throws Exception;
	
	NotificationConfigDTO findBySubCategory(String subCategory) throws Exception;
	
	List<NotificationConfigDTO> findAll() throws Exception;

}
