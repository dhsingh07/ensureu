package com.book.ensureu.service;

import java.util.List;

import com.book.ensureu.model.UserRole;

public interface UserRolesService {

	public UserRole getUserRoleById(String id);

	public void saveUserRoles(List<UserRole> userRoles);

	public void saveUserRoles(UserRole userRole);

}
