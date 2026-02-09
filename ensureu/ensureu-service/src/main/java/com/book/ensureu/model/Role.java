package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.RoleType;

@Document(collection="role")
public class Role implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7901478633240459659L;
	@Id
	private String id;
	private RoleType roleType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public RoleType getRoleType() {
		return roleType;
	}
	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
	@Override
	public String toString() {
		return "Role [id=" + id + ", roleType=" + roleType + "]";
	}

	
	
}
