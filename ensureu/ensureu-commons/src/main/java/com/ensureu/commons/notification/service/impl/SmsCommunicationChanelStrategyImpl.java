package com.ensureu.commons.notification.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.service.ClickSendSMSProvider;
import com.ensureu.commons.notification.service.CommunicationChanelStrategy;
@Service
@Qualifier("smsCommunicationChanel")
public class SmsCommunicationChanelStrategyImpl implements CommunicationChanelStrategy {

	private static final Logger LOGGER=LoggerFactory.getLogger(SmsCommunicationChanelStrategyImpl.class.getName());
	
	private  static final ClickSendSMSProvider clickSendSMSProvider=ClickSendSMSProviderService.getInstance();
	@Override
	public void sendNotification(Notification notification) throws Exception {
		System.out.println("Send SMS notification ....");
		LOGGER.info("SMS notification");
		clickSendSMSProvider.sendSMS(notification);
	}

	@Override
	public void sendNotificationAsynch(Notification notification) throws Exception {
		System.out.println("Asynch Send SMS notification ....");
		LOGGER.info("SMS Asynch notification");
		clickSendSMSProvider.sendSMSAsynch(notification);
	}

}
