package com.book.ensureu.api;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.model.Role;
import com.book.ensureu.model.UserRole;
import com.book.ensureu.service.RolesService;
import com.book.ensureu.service.UserRolesService;

@CrossOrigin
@RequestMapping("/role")
@RestController
public class UserRoleApi {
	
	@Autowired
	private RolesService rolesService;
	
	@Autowired
	private UserRolesService userRolesService;
	
	@RequestMapping(value="/saveroles",method=RequestMethod.POST)
	public void saveRoles(@RequestBody final List<Role> roles)
	{
		rolesService.saveRoles(roles);
	}
	
	@RequestMapping(value="/saverole",method=RequestMethod.POST)
	public void saveRoles(@RequestBody final Role role)
	{
		rolesService.saveRoles(role);
	}
	
	/*@RequestMapping(value="/getRoles",method=RequestMethod.GET)
	public void saveRoles()
	{
		
	}*/
	

	@RequestMapping(value="/getrole",method=RequestMethod.GET)
	public Role getRole(@RequestParam(value="id") final String id)
	{
		Role role=rolesService.getRolesById(id);
		String roleName=role.getRoleType().toString();
		System.out.println(roleName);
		
		return role;
	}
	
	@RequestMapping(value="/saveuserroles",method=RequestMethod.POST)
	public void saveUserRoles(@RequestBody final List<UserRole> userRoles)
	{
		userRolesService.saveUserRoles(userRoles);
	}
	
	@RequestMapping(value="/saveuserrole",method=RequestMethod.POST)
	public void saveRoles(@RequestBody final UserRole userRole)
	{
		userRolesService.saveUserRoles(userRole);
	}
	

}
