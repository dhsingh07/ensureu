package com.ensureu.commons.notification.service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.reposne.Response;

public interface ClickSendEmailProvider {
	public Response sendEmail(Notification notification);

	public void sendEmailAsync(Notification notification);
}
