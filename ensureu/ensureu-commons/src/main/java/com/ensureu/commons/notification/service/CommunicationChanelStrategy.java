package com.ensureu.commons.notification.service;

import com.ensureu.commons.notification.data.Notification;

public interface CommunicationChanelStrategy {
	public void sendNotification(Notification notification) throws Exception;
	public void sendNotificationAsynch(Notification notification) throws Exception;

}
