package com.ensureu.commons.notification.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ensureu.commons.conf.PropertiesFileConfig;
import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.reposne.Response;
import com.ensureu.commons.notification.data.sms.SmsNotification;
import com.ensureu.commons.notification.service.ClickSendSMSProvider;

import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Api.SmsApi;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;

public class ClickSendSMSProviderService implements ClickSendSMSProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClickSendSMSProviderService.class);

	private ApiClient apiClient;
	
	private SmsApi smsApi;

	private String key;

	private String userName;

	private ClickSendSMSProviderService() {
		apiClient = new ApiClient();
		key = PropertiesFileConfig.getPropertyValue("spring.notification.key");
		userName = PropertiesFileConfig.getPropertyValue("spring.notification.userName");
		apiClient.setUsername(userName);
		apiClient.setPassword(key);
		smsApi = new SmsApi(apiClient);
	}

	public static class ClickSendSMSProviderSinglton {
		public static final ClickSendSMSProvider _instance = new ClickSendSMSProviderService();
	}

	public static ClickSendSMSProvider getInstance() {
		return ClickSendSMSProviderSinglton._instance;
	}

	@Override
	public Response sendSMS(Notification notification) {
		System.out.println("send SMS..........");
		System.out.println("username " + userName);
		LOGGER.info("userName " + userName);
		SmsNotification smsNotification = (SmsNotification) notification;
		String message = smsNotification.getMessage().getContent().toString();
		String toAddress = smsNotification.getFromNumber();
		List<SmsMessage> listSms = null;
		String response = null;
		if (smsNotification != null && smsNotification.getReciverList() != null
				&& smsNotification.getReciverList().size() > 0) {
			List<String> smsReciverList = smsNotification.getReciverList();
			listSms = new ArrayList<>();
			for (String reciver : smsReciverList) {
				SmsMessage smsMessage = new SmsMessage();
				smsMessage.setBody(message);
				smsMessage.setTo(reciver);
				smsMessage.setSource(toAddress);
				listSms.add(smsMessage);
			}
			SmsMessageCollection smsMessageCollection = new SmsMessageCollection();
			smsMessageCollection.messages(listSms);

			try {
				response = smsApi.smsSendPost(smsMessageCollection);
				LOGGER.info("Response " + response);
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new IllegalAccessException("Reciver List can't be null");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return new Response.ResponseBuilder<String>(200, response).build();
	}

	@Override
	public void sendSMSAsynch(Notification notification) {

	}

}
