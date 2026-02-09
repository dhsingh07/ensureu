package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.UserEntitlement;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.UserEntitlementService;

@CrossOrigin
@RequestMapping("/entitlement")
@RestController
public class UserEntitlementApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserEntitlementApi.class.getName());

	@Autowired
	private UserEntitlementService userEntitlementService;

	@RequestMapping(value = "/getUserEntitle/{userId}", method = RequestMethod.POST)
	public Response<List<UserEntitlement>> getEntitlement(@PathVariable("userId") String userId,
			@RequestParam(value = "active", defaultValue = "true") Boolean active) {
		LOGGER.info(" in method getEntitlement for userId: {}", userId);
		return userEntitlementService.getUserEntitlement(userId, active);
	}
	
	@Scheduled(cron = "0 0 0 * * *")
	@RequestMapping(value = "/movePapers", method = RequestMethod.GET)
	public void movePaperToSubscription() {
		LOGGER.info(" inside cron movePaperToSubscription method: ");
	//	userEntitlementService.updateUserEntitles(); 
	}
}
