package com.book.ensureu.service;

import javax.mail.MessagingException;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

	public void sendSimpleMessage(String to,String subject,String messageBody) throws MailException;
	public void sendMessageUsingTemplate(String to,String subject,SimpleMailMessage template,String ... templateArgs) throws MailException;
	public void sendMessageWithAttachedment(String to,String subject,String messageBody,String attachedPath) throws MessagingException,MailException;
	
	public SimpleMailMessage getSimpleMailTemplateMessage();
}
