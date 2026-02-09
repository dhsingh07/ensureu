package com.book.ensureu.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;

import com.book.ensureu.constant.ServiceName;
import com.book.ensureu.constant.UserLoginType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class ServiceCallTraceDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7290371296166582868L;
	private String id;
	private String username;
	private ServiceName serviceName;
	private int serviceCallCount;
	private int serviceCallLimit;
	@Indexed(name = "expire_after_seconds_index", expireAfterSeconds = 3600*24)
	private LocalDateTime expirySeviceCallTime;
	private UserLoginType loginType;
	
	
	public ServiceCallTraceDto() {
		super();
	}


	public ServiceCallTraceDto(String id, String username, ServiceName serviceName, int serviceCallCount,
			int serviceCallLimit, UserLoginType loginType) {
		super();
		this.id = id;
		this.username = username;
		this.serviceName = serviceName;
		this.serviceCallCount = serviceCallCount;
		this.serviceCallLimit = serviceCallLimit;
		this.loginType = loginType;
	}
	
	
		
}
