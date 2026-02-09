package com.book.ensureu.api;

import java.util.List;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.UserPassSubscriptionDto;
import com.book.ensureu.model.UserPass;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.UserPassService;

@RestController
@RequestMapping("/pass")
public class UserPassApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPassApi.class.getName());

	@Autowired
	private UserPassService userPassService;
	
	@Autowired
	UserPrincipalService userPrincipalService;
	
	@CrossOrigin
	@GetMapping("/list")
	public Response<List<UserPass>> getUserPassList(){
		LOGGER.info(" inside getUserPassList method ");
 
		return new Response<List<UserPass>>()
				 .setBody(userPassService.getAllActivePass()).
				 setStatus(200).
				 setMessage("fetch successfully");
		 
		 
		
	}
	
	@CrossOrigin
	@RequestMapping(value = "/getAllPass", method = RequestMethod.GET)
	public  Response<List<UserPass>> getPassList() {
		LOGGER.info(" inside getPassList method ");
		List<UserPass> list =  userPassService.getAllActivePass();
		
		return new Response<List<UserPass>>()
				.setStatus(200)
				.setBody(list)
				.setMessage("fetch successfully ");
	}
	
	@CrossOrigin
	@PostMapping("/subscribe/{userId}")
	public Response<String> subcribe(@PathParam("userId") String userId,
			@RequestBody UserPassSubscriptionDto subcriptionDto ){
		LOGGER.info(" inside pass subcribe method userId ");
	Response<String> response =	userPassService.subscribe(userId,subcriptionDto);
	return  response;
	}
	
	@CrossOrigin
	@PostMapping("/subscribe")
	public Response<String> subcribe(@RequestBody UserPassSubscriptionDto subcriptionDto ){
		LOGGER.info(" inside pass subcribe method  ");
	String userId=userPrincipalService.getCurrentUserDetails().getUsername();
	Response<String> response =	userPassService.subscribe(userId,subcriptionDto);
	return  response;
	}
	
	
}
