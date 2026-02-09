package com.ensureu.commons.notification.util;


import java.util.Arrays;
import java.util.List;

import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Api.SmsApi;
import ClickSend.Api.TransactionalEmailApi;
import ClickSend.Model.Email;
import ClickSend.Model.EmailFrom;
import ClickSend.Model.EmailRecipient;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;

public class EmailUtil {

	private ApiClient apiClient=null;
	
	public EmailUtil() {
		
		 apiClient = new ApiClient();
		 apiClient.setUsername("dhsingh07@gmail.com");
		 apiClient.setPassword("6998B365-FFBB-D768-22AD-48E608D6BF9C");
	}
	
	public void sendSms() 
	{
		
		SmsApi smsApi=new SmsApi(apiClient);
		SmsMessage smsMessage=new SmsMessage();
		smsMessage.setBody("Hi Mr A");
		smsMessage.setTo("+61411111111");
		smsMessage.setSource("abc");
		
		List<SmsMessage> listSms=Arrays.asList(smsMessage);
		SmsMessageCollection smsMessageCollection=new SmsMessageCollection();
		smsMessageCollection.messages(listSms);
		try {
			String response=smsApi.smsSendPost(smsMessageCollection);
			System.out.println(response);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendEmail() {
		/*ApiClient defaultClient = new ApiClient();
	    defaultClient.setUsername("dhsingh07@gmail.com");
	    defaultClient.setPassword("6998B365-FFBB-D768-22AD-48E608D6BF9C");
*/	    TransactionalEmailApi apiInstance = new TransactionalEmailApi(apiClient);
	    EmailRecipient emailRecipient=new EmailRecipient();
	    emailRecipient.email("test3@test.com");
	    emailRecipient.name("abc");
	    List<EmailRecipient> emailRecipientList=Arrays.asList(emailRecipient);
	    EmailFrom emailFrom=new EmailFrom();
	    emailFrom.emailAddressId("6311");
	    emailFrom.name("DK Singh");
	   /* Attachment attachment= new Attachment();
	    attachment.content("ZmlsZSBjb250ZW50cw==");
	    attachment.contentId("text");
	    attachment.disposition("attachment");
	    attachment.type("text/plain");
	    attachment.filename("text.txt");
	    List<Attachment> attachmentList=Arrays.asList(attachment);*/
	    Email email = new Email(); // Email | Email model
	    email.to(emailRecipientList);
	    email.cc(emailRecipientList);
	    email.bcc(emailRecipientList);
	    email.from(emailFrom);
	    email.subject("Test Mail");
	    email.body("Test mail body");
	   // email.attachments(attachmentList);
	   // email.schedule(new BigDecimal(147258369));
	    try {
	    	System.out.println(email);
	        String result = apiInstance.emailSendPost(email);
	        System.out.println(result);
	    } catch (ApiException e) {
	        System.err.println("Exception when calling TransactionalEmailApi#emailSendPost");
	        e.printStackTrace();
	    }
}

	public static void main(String args[]) {
		EmailUtil emailUtil=new EmailUtil();
		emailUtil.sendEmail();;
	}
}
