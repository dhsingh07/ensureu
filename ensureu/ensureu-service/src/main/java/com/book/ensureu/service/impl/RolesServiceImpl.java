package com.book.ensureu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.Role;
import com.book.ensureu.repository.RolesRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.RolesService;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class RolesServiceImpl implements RolesService {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(RolesServiceImpl.class);

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private CounterService counterService;
	
	@Override
	public void saveRoles(List<Role> roles) {

		if (roles != null && !roles.isEmpty()) {
			rolesRepository.saveAll(roles);
		}
	}

	@Override
	public void saveRoles(Role role) {
		if (role != null) {
			rolesRepository.save(role);
		}
	}

	@Override
	public Role getRolesById(String id) {
		return rolesRepository.findById(id).get();
	}

	
}
