package com.book.ensureu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.UserRole;
import com.book.ensureu.repository.UserRolesRepository;
import com.book.ensureu.service.UserRolesService;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class UserRolesServiceImpl implements UserRolesService {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(UserRolesServiceImpl.class);
	@Autowired
	UserRolesRepository userRolesRepository;
	@Override
	public UserRole getUserRoleById(String id) {
		return userRolesRepository.findById(id).get();
	}

	@Override
	public void saveUserRoles(List<UserRole> userRoles) {
		if(userRoles!=null && !userRoles.isEmpty()) {
			userRolesRepository.saveAll(userRoles);
		}
	}

	@Override
	public void saveUserRoles(UserRole userRole) {
		userRolesRepository.save(userRole);
	}

}
