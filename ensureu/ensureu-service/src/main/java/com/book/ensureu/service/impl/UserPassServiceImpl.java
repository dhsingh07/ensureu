package com.book.ensureu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.ApplicationConstant;
import com.book.ensureu.dto.UserPassSubscriptionDto;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.repository.UserPassRepository;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.UserPassService;
import com.book.ensureu.service.impl.helper.SubscriptionServiceImplHelper;

@Service
public class UserPassServiceImpl implements UserPassService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPassServiceImpl.class.getName());

	@Autowired
	private UserPassRepository userPassRepository;

	@Autowired
	SubscriptionServiceImplHelper subscriptionServiceImplHelper;

	public List<UserPass> getAllPass() {
		return userPassRepository.findAll();

	}

	public List<UserPass> getAllActivePass() {
		return userPassRepository.findByActive(true);

	}

	@Override
	public Response<String> subscribe(String userId, UserPassSubscriptionDto subcriptionDto) {
		LOGGER.info(
				" inside subscribe method userId: " + userId + " pass buy: " + subcriptionDto.getSubscriptionType());

		Response<String> response = new Response<>();
		String body = null;
		int status = 200;
		try {
			subscriptionServiceImplHelper.createUserEntitlementAndSave(userId, subcriptionDto);
			body = ApplicationConstant.SUBSCRIBED_MESSAGE;
		} catch (Exception e) {
			LOGGER.error(" Exception occured while pass subscription : " + e.getMessage());
			body = " Exception occurs while subcription";
			status = 500;

		}
		response.setBody(body).setStatus(status);
		return response;
	}

	

}
