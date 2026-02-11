package com.ensureu.commons.notification.data.sms;

import java.util.List;

import com.ensureu.commons.constant.NotificationType;
import com.ensureu.commons.notification.data.Message;
import com.ensureu.commons.notification.data.Notification;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonIgnoreProperties
public class SmsNotification extends Notification {

	private List<String> reciverList;
	private String fromNumber;
	
	
	public SmsNotification() {
		super();
	}

	public SmsNotification(Message<?> message, NotificationType notificationType) {
		super(message, notificationType);
	}

	public SmsNotification(Message<?> message, NotificationType notificationType, List<String> reciverList,
			String fromNumber) {
		super(message, notificationType);
		this.reciverList = reciverList;
		this.fromNumber = fromNumber;
	}

	public List<String> getReciverList() {
		return reciverList;
	}

	public String getFromNumber() {
		return fromNumber;
	}

	@Override
	public String toString() {
		return "SmsNotification [reciverList=" + reciverList + ", fromNumber=" + fromNumber + "]";
	}
	
	

}
