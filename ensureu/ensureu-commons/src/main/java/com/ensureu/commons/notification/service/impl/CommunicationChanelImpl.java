package com.ensureu.commons.notification.service.impl;

import org.springframework.stereotype.Service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.service.AbstractCommunicationChanel;

@Service
public class CommunicationChanelImpl extends AbstractCommunicationChanel {
	
	public CommunicationChanelImpl() {
		super();
	}
	
	public CommunicationChanelImpl(Notification notification) {
		super(notification);
	}
	
}
