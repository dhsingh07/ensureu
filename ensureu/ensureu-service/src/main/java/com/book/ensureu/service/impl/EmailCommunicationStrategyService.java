package com.book.ensureu.service.impl;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.EmailMessage;
import com.book.ensureu.dto.Message;
import com.book.ensureu.service.CommunicationChannel;
import com.book.ensureu.service.CommunicationChannelStrategy;
import com.book.ensureu.service.EmailService;

@Service
@Qualifier("emailCommunication")
public class EmailCommunicationStrategyService implements CommunicationChannelStrategy {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EmailCommunicationStrategyService.class);

	@Autowired
	private EmailService emailService;

	@Autowired
	CommunicationChannel communicationChannel;

	public EmailCommunicationStrategyService() {

	}

	@Override
	public void sendMessage(Message message) {
		System.out.println("EmailCommunicationStrategyService message");
		try {
			EmailMessage mess = (EmailMessage) message;
			emailService.sendSimpleMessage(mess.getTo(), mess.getSubject(), mess.getMessage());
		} catch (Exception ex) {
			LOGGER.error("Error while sending email ", ex);
		}
	}

	/*
	 * public void sendEmail(String to, String subject) {
	 * CommunicationChannelStrategy emailCommunication = (message) ->
	 * emailService.sendSimpleMessage(to, subject, message); }
	 */

}
