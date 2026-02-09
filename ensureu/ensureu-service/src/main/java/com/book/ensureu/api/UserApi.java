package com.book.ensureu.api;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.UserDto;
import com.book.ensureu.model.User;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.UserService;

/**
 * @author dharmendra.singh
 *
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserApi.class);

	@Autowired
	UserService userService;
	
	@Lazy
	@Autowired
	UserPrincipalService userPrincipal;

	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Response<String> createAndSaveUser(@RequestBody UserDto userDto) throws Exception {
		LOGGER.info("Create User");
		try {
			userService.createUser(userDto);
			return new Response<String>()
					.setStatus(200)
					.setMessage("Success");

		} catch (Exception e) {
			throw e;
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public UserDto getUserById(@PathVariable(value = "id") final Long id) {
		LOGGER.info("find Id{}",id);
		try {
		return userService.getUserDtoById(id);
		}catch(Exception e) {
			LOGGER.error("getUserById "+ id,e);
		}
		LOGGER.info("User dos'nt exist");
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/getbyusername/{userName}", method = RequestMethod.GET)
	public Response<UserDto> getUserById(@PathVariable(value = "userName") final String userName) {
		LOGGER.info("find userName"+userName);
		try {
			UserDto userDto =  userService.getUserDtoByUserName(userName);
		return new Response<UserDto>().setStatus(200).setMessage("Success").setBody(userDto);
		}catch(Exception e) {
			LOGGER.error("getUserByName "+ userName,e);
		}
		LOGGER.info("User dos'nt exist");
		return null;
	}
	
	@CrossOrigin
	@RequestMapping(value = "/getbyusername", method = RequestMethod.GET)
	public Response<UserDto> getUserByUserName(HttpServletRequest httpRequest) {

		LOGGER.info("find userName" + userPrincipal.getCurrentUserDetails().getUsername());
		try {
			UserDto userDto = userService.getUserDtoByUserName(userPrincipal.getCurrentUserDetails().getUsername());
			return new Response<UserDto>().setStatus(200).setMessage("Success").setBody(userDto);
		} catch (Exception e) {
			LOGGER.error("getUserByName " + userPrincipal.getCurrentUserDetails().getUsername(), e);
		}
		LOGGER.info("User dos'nt exist");
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/savepresent", method = RequestMethod.POST)
	public Response<String> creatIfNotpreset(@RequestBody UserDto userDto) throws Exception {
		
		try {
			LOGGER.info("savePresent User");
			Optional<User> dbUser = userService.getUserByUserName(userDto.getUserName());
			if (dbUser.isPresent()) {
				LOGGER.info("User already present " + dbUser.get().getId() + "User Name " + userDto.getUserName());
				return new Response<String>()
				.setStatus(200)
				.setMessage("User already present");
			} else {
				userService.createUser(userDto);
				return new Response<String>()
						.setStatus(200)
						.setMessage("Success");
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/profile/update", method = RequestMethod.POST)
	public Response<String> updateUser(@RequestBody UserDto userDto) throws Exception {
		LOGGER.info("Create User");
		try {
			userService.updateUser(userDto);
			return new Response<String>()
					.setStatus(200)
					.setMessage("Success");
		} catch (Exception e) {
			throw e;
		}
	}

}
