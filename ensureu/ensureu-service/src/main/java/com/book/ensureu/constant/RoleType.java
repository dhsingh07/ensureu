package com.book.ensureu.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author dharmendra.singh
 *
 */
public enum RoleType {
	USER, ADMIN, SUPERADMIN, TEACHER;

	@JsonCreator
	public static RoleType fromString(String value) {
		if (value == null) {
			return null;
		}
		for (RoleType role : RoleType.values()) {
			if (role.name().equalsIgnoreCase(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("No enum constant " + RoleType.class.getName() + "." + value);
	}

	@Override
	public String toString() {
		switch (this) {
		case USER:
			return "ROLE_USER";
		case ADMIN:
			return "ROLE_ADMIN";
		case SUPERADMIN:
			return "ROLE_SUPERADMIN";
		case TEACHER:
			return "ROLE_TEACHER";
		default:
			throw new IllegalArgumentException("Input is invalid");
		}
	}
}
