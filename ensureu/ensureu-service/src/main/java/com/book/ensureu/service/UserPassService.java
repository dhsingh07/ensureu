package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.dto.UserPassSubscriptionDto;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.response.dto.Response;

public interface UserPassService {

	public List<UserPass> getAllPass();
	
	public List<UserPass> getAllActivePass();

	public Response<String> subscribe(String userId, UserPassSubscriptionDto subcriptionDto);

}
