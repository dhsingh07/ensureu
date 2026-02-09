package com.book.ensureu.model;

import com.book.ensureu.constant.UserType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserTenant extends BaseModel {
	
	private String userId;
	private String email;
	private int shardNumber;
	private String shardId;
	private String tenantId;
	private UserType userType;

}
