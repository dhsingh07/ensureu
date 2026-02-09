package com.ensureu.commons.notification.service;


import org.springframework.stereotype.Service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.email.EmailNotification;
import com.ensureu.commons.notification.data.sms.SmsNotification;
import com.ensureu.commons.notification.service.impl.EmailCommunicationChanelStrategyImpl;
import com.ensureu.commons.notification.service.impl.SmsCommunicationChanelStrategyImpl;

@Service
public abstract class AbstractCommunicationChanel {

	private CommunicationChanelStrategy communicationChanelStrategy;
	private Notification notification;

	
	public AbstractCommunicationChanel() {
		super();
	}

	public AbstractCommunicationChanel(
			Notification notification) {
		super();
		this.notification = notification;
		this.communicationChanelStrategy=setCommunicationChanel(notification);
	}
	
	public AbstractCommunicationChanel(CommunicationChanelStrategy communicationChanelStrategy,
			Notification notification) {
		super();
		this.communicationChanelStrategy = communicationChanelStrategy;
		this.notification = notification;
	}

	public void doCommunicate() throws Exception {
		communicationChanelStrategy.sendNotification(notification);
	}

	public void doCommunicateAsynch() throws Exception {
		communicationChanelStrategy.sendNotificationAsynch(notification);
	}

	public void setCommunicationChanelStrategy(CommunicationChanelStrategy communicationChanelStrategy) {
		this.communicationChanelStrategy = communicationChanelStrategy;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	private CommunicationChanelStrategy setCommunicationChanel(Notification notification) {
		if(notification instanceof EmailNotification) {
			communicationChanelStrategy= new EmailCommunicationChanelStrategyImpl();
		}else if(notification instanceof SmsNotification) {
			communicationChanelStrategy= new SmsCommunicationChanelStrategyImpl();
		}else {
			throw new IllegalArgumentException("Notificatio  type not valid");
		}
		return communicationChanelStrategy;
		
	}

}
