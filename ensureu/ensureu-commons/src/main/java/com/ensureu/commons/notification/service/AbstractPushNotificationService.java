package com.ensureu.commons.notification.service;

import com.ensureu.commons.notification.data.Notification;

public abstract class AbstractPushNotificationService {

	protected Notification notification;
	
	
	public AbstractPushNotificationService() {
		super();
	}

	public AbstractPushNotificationService(Notification notification) {
		super();
		this.notification = notification;
	}
	
	public void pushNotifiction() throws Exception{
		doPushNotification(notification);
	}
	public abstract void doPushNotification(Notification notification) throws Exception;
}
