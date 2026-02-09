package com.book.ensureu.service;

import java.util.Optional;

import com.book.ensureu.constant.UserLoginType;
import com.book.ensureu.dto.UserDto;
import com.book.ensureu.model.User;

public interface UserService {

	public void createUser(User user) throws Exception;
	
	public void createUser(UserDto userDto) throws Exception;

	public void updateUser(User user) throws Exception;

	public Optional<User> getUserById(Long id) throws Exception;
	
	public void updateUser(UserDto userDto) throws Exception;

	public UserDto getUserDtoById(Long id) throws Exception;

	public Optional<User> getUserByUserName(String userName) throws Exception;
	
	public UserDto getUserDtoByUserName(String userName) throws Exception;

	void saveUser(User user);

	/**
	 * Find existing user or create new one for provider authentication (Google/Facebook).
	 * Used when user logs in via OAuth provider - creates user with dummy password if not exists.
	 *
	 * @param email User's email from provider
	 * @param name User's display name from provider
	 * @param loginType The provider type (GOOGLE, FACEBOOK)
	 * @return The existing or newly created user
	 */
	User findOrCreateProviderUser(String email, String name, UserLoginType loginType);

}
