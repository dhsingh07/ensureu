package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.model.Role;

/**
 * @author dharmendra.singh
 *
 */
public interface RolesService {

	public void saveRoles(List<Role> roles);

	public void saveRoles(Role roles);

	public Role getRolesById(String id);

}
