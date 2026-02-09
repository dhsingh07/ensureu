package com.ensureu.commons.notification.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.service.ClickSendEmailProvider;
import com.ensureu.commons.notification.service.CommunicationChanelStrategy;

@Service
@Qualifier("emailCommunicationChanel")
public class EmailCommunicationChanelStrategyImpl implements CommunicationChanelStrategy {

	private static final Logger LOGGER=LoggerFactory.getLogger(EmailCommunicationChanelStrategyImpl.class.getName());
	private final ClickSendEmailProvider clickSendEmailProvider=ClickSendEmailProviderService.getInstance();
	

	@Override
	public void sendNotification(Notification notification) throws Exception {
		System.out.println("Send Email notification ....");
		LOGGER.info("Send Email notification ....");
		clickSendEmailProvider.sendEmail(notification);
		
	}

	@Override
	public void sendNotificationAsynch(Notification notification) throws Exception {
		System.out.println("Asynch Send Email notification ....");
		LOGGER.info("Send Asynch Email notification ....");
		clickSendEmailProvider.sendEmailAsync(notification);
	}

}
