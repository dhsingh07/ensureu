package com.ensureu.commons.notification.data;

import com.ensureu.commons.constant.NotificationType;
import com.ensureu.commons.constant.PlatformType;
import com.ensureu.commons.notification.data.sms.SmsNotification;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonIgnoreProperties
public class Notification {

	private Long id;
	private Message<?> message;
	private NotificationType notificationType;
	private PlatformType plateformType;
	private Long createdDate;
	private String createdBy;
	
	
	public Notification() {
		super();
	}

	public Notification(Message<?> message, NotificationType notificationType) {
		super();
		this.message = message;
		this.notificationType = notificationType;
	}

	public Notification(Message<?> message, NotificationType notificationType,
			String createdBy) {
		super();
		this.message = message;
		this.notificationType = notificationType;
		this.createdBy = createdBy;
	}

	public Notification(Message<?> message, NotificationType notificationType, PlatformType plateformType,
			Long createdDate, String createdBy) {
		super();
		this.message = message;
		this.notificationType = notificationType;
		this.plateformType = plateformType;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
	}
	
	

	public Notification(Long id, Message<?> message, NotificationType notificationType, PlatformType plateformType,
			Long createdDate, String createdBy) {
		super();
		this.id = id;
		this.message = message;
		this.notificationType = notificationType;
		this.plateformType = plateformType;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
	}

	public Message<?> getMessage() {
		return message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public PlatformType getPlateformType() {
		return plateformType;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Notification [message=" + message + ", notificationType=" + notificationType + ", plateformType="
				+ plateformType + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + "]";
	}
	
	
	

	
}
