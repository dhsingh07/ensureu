package com.ensureu.commons.notification.service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.reposne.Response;

public interface ClickSendSMSProvider {
	
	public Response sendSMS(Notification notification);
	
	public void sendSMSAsynch(Notification notification);
	
	
}
