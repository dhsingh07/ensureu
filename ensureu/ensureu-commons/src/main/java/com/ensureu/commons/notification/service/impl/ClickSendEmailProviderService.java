package com.ensureu.commons.notification.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ensureu.commons.conf.PropertiesFileConfig;
import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.email.EmailNotification;
import com.ensureu.commons.notification.data.reposne.Response;
import com.ensureu.commons.notification.service.ClickSendEmailProvider;

import ClickSend.ApiCallback;
import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Api.TransactionalEmailApi;
import ClickSend.Model.Email;
import ClickSend.Model.EmailFrom;
import ClickSend.Model.EmailRecipient;

public class ClickSendEmailProviderService implements ClickSendEmailProvider {
	
	private final static Logger LOGGER=LoggerFactory.getLogger(ClickSendEmailProviderService.class.getName());

	private TransactionalEmailApi transactionalEmailApi;
	private ApiClient apiClient;

	private String key;
	private String userName;
	private String emailId = "6311";

	@Autowired
	private ClickSendEmailProviderService() {
		key=PropertiesFileConfig.getPropertyValue("spring.notification.key");
		userName=PropertiesFileConfig.getPropertyValue("spring.notification.userName");
		emailId=PropertiesFileConfig.getPropertyValue("spring.notification.emailId");
		apiClient = new ApiClient();
		apiClient.setUsername(userName);
		apiClient.setPassword(key);
		transactionalEmailApi = new TransactionalEmailApi(apiClient);
	}

	public static class ClickSendEmailProviderSinglton {
		public static final ClickSendEmailProvider instance = new ClickSendEmailProviderService();
	}

	public static ClickSendEmailProvider getInstance() {
		return ClickSendEmailProviderSinglton.instance;
	}

	@Override
	public Response sendEmail(Notification notification) {

		String response=null;
		LOGGER.info("sendEmail in progress...");
		if (notification != null) {
			LOGGER.info("notifactionType "+notification.getNotificationType());
			EmailNotification emailNotification = (EmailNotification) notification;
			Email email=createEmail(emailNotification);
			// emailRecipient.setEmail(notification);
			try {
				response=transactionalEmailApi.emailSendPost(email);
			} catch (ApiException e) {
				e.printStackTrace();
			}

		}
		return new Response.ResponseBuilder<String>(200, response).build();
	}

	@Override
	public void sendEmailAsync(Notification notification) {
		
		LOGGER.info("sendEmailAsync in progress...");
		if(notification!=null) {
			EmailNotification emailNotification=(EmailNotification)notification;
			Email email=createEmail(emailNotification);
			
			try {
				transactionalEmailApi.emailSendPostAsync(email, new ApiCallback<String>() {

					@Override
					public void onDownloadProgress(long arg0, long arg1, boolean arg2) {
						
					}
					@Override
					public void onFailure(ApiException arg0, int arg1, Map<String, List<String>> arg2) {
						LOGGER.error(arg0.getMessage(),"code "+arg1,arg2);
					}
					@Override
					public void onSuccess(String arg0, int arg1, Map<String, List<String>> arg2) {
						LOGGER.info(arg0,"code "+arg1,arg2);
					}
					@Override
					public void onUploadProgress(long arg0, long arg1, boolean arg2) {
						LOGGER.info(arg0+"",arg1,arg2);
					}
				});
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
	}

	private void addEmailRecipient(List<EmailRecipient> emailRecipients, Collection<String> address) {
		if (address != null) {
			for (String email : address) {
				EmailRecipient emailRecipient = new EmailRecipient();
				emailRecipient.setEmail(email);
				emailRecipients.add(emailRecipient);
			}
		}
	}
	
	
	
	/**
	 * @param emailNotification
	 * @return
	 */
	private Email createEmail(EmailNotification emailNotification) {
		Map<String, String> emailList = emailNotification.getReciverEmailVsName();
		List<EmailRecipient> toEmailRecipients = new ArrayList<EmailRecipient>();
		if (emailList != null && !emailList.isEmpty()) {

			emailList.forEach((email, name) -> {
				EmailRecipient emailRecipient = new EmailRecipient();
				emailRecipient.setEmail(email);
				emailRecipient.setName(name);
				toEmailRecipients.add(emailRecipient);
			});

			// Form email set...
			EmailFrom emailFrom = new EmailFrom();
			emailFrom.setEmailAddressId(emailId);
			emailFrom.name("Ensureu");

		}
		Email email = new Email();
		email.setTo(toEmailRecipients);

		Collection<String> ccEmails = emailNotification.getCcAddrress();
		if (ccEmails != null && !ccEmails.isEmpty()) {
			// cc email recipient..
			List<EmailRecipient> ccEmailsRecipient = new LinkedList<>();
			addEmailRecipient(ccEmailsRecipient, ccEmails);
			email.setCc(ccEmailsRecipient);
		}

		Collection<String> bccEmails = emailNotification.getBccAddress();

		if (bccEmails != null && !bccEmails.isEmpty()) {
			// bcc email recipient..
			List<EmailRecipient> bccEmailsRecipient = new LinkedList<>();
			addEmailRecipient(bccEmailsRecipient, bccEmails);
			email.setBcc(bccEmailsRecipient);
		}
		return email;
	}

}
