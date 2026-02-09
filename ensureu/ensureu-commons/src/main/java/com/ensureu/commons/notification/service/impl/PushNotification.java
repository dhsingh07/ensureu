package com.ensureu.commons.notification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.service.AbstractPushNotificationService;
import com.ensureu.commons.notification.service.PushNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PushNotification extends AbstractPushNotificationService implements PushNotificationService{

	
	/*@Autowired
	private SimpMessagingTemplate messageTemplate;
	*/
	@Autowired
	private ObjectMapper objectMapper;
	
/*	@Autowired
	private Notification notification;
	*/
	
	public PushNotification() {
		super();
	}

	public PushNotification(Notification notification) {
		super(notification);
	}

	@Override
	public void doPushNotification(Notification notification) throws Exception{
		/*try {
			messageTemplate.convertAndSend("/topic/values", objectMapper.writeValueAsString(notification));
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public void pushNotification() {
		
	}

}
