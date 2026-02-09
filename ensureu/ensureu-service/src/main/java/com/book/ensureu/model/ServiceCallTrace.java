package com.book.ensureu.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.constant.ServiceName;
import com.book.ensureu.constant.UserLoginType;

@Document(collection="serviceCallTrace")
public class ServiceCallTrace {

	private String id;
	private String username;
	private ServiceName serviceName;
	private int serviceCallCount;
	private int serviceCallLimit;
	@Indexed(name = "expire_after_seconds_index", expireAfterSeconds = 3600*24)
	private LocalDateTime expirySeviceCallTime;
	private UserLoginType loginType;
	private LocalDateTime createDateTime;
	private LocalDateTime modifiedDateTime;
	
	public ServiceCallTrace() {
		super();
	}

	public ServiceCallTrace(String id, String username, ServiceName serviceName, int serviceCallCount,
			int serviceCallLimit, UserLoginType loginType) {
		super();
		this.id = id;
		this.username = username;
		this.serviceName = serviceName;
		this.serviceCallCount = serviceCallCount;
		this.serviceCallLimit = serviceCallLimit;
		this.loginType = loginType;
	}

	public ServiceCallTrace(String id, String username, ServiceName serviceName, int serviceCallCount,
			int serviceCallLimit, LocalDateTime expirySeviceCallTime, UserLoginType loginType,
			LocalDateTime createDateTime) {
		super();
		this.id = id;
		this.username = username;
		this.serviceName = serviceName;
		this.serviceCallCount = serviceCallCount;
		this.serviceCallLimit = serviceCallLimit;
		this.expirySeviceCallTime = expirySeviceCallTime;
		this.loginType = loginType;
		this.createDateTime = createDateTime;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public ServiceName getServiceName() {
		return serviceName;
	}

	public int getServiceCallCount() {
		return serviceCallCount;
	}

	public int getServiceCallLimit() {
		return serviceCallLimit;
	}

	public LocalDateTime getExpirySeviceCallTime() {
		return expirySeviceCallTime;
	}

	public UserLoginType getLoginType() {
		return loginType;
	}

	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	public void setServiceCallCount(int serviceCallCount) {
		this.serviceCallCount = serviceCallCount;
	}

	public void setServiceCallLimit(int serviceCallLimit) {
		this.serviceCallLimit = serviceCallLimit;
	}

	public void setExpirySeviceCallTime(LocalDateTime expirySeviceCallTime) {
		this.expirySeviceCallTime = expirySeviceCallTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getModifiedDateTime() {
		return modifiedDateTime;
	}

	public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	
	
}
