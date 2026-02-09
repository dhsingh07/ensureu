package com.book.ensureu.api;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.MailDto;
import com.book.ensureu.service.EmailService;

@RestController
@RequestMapping("/mail")
public class EmailApi {

	@Autowired
	private EmailService emailService;
	
	@RequestMapping(value="/sendSimple", method=RequestMethod.POST)
	public void sendEmailMessage(@RequestBody MailDto mail) {

		try {
			emailService.sendSimpleMessage(mail.getTo(), mail.getSubject(), mail.getMessage());
		
		} catch (MailException e) {
			throw e;
		}
	}
	
	@RequestMapping(value="/sendTemplate", method=RequestMethod.POST)
	public void sendMessageUsingTemplate(@RequestBody MailDto mail) {

		try {
			SimpleMailMessage messageTemp=emailService.getSimpleMailTemplateMessage();
			emailService.sendMessageUsingTemplate(mail.getTo(), mail.getSubject(),messageTemp, mail.getMessage());
		} catch (MailException e) {
			throw e;
		}
	}
	
	
	@RequestMapping(value="/sendAttachment", method=RequestMethod.POST)
	public void sendAttachmentMessage(@RequestBody MailDto mail) {

		String attachedPath="/logback.xml";
		
		try {
			emailService.sendMessageWithAttachedment(mail.getTo(), mail.getSubject(), mail.getMessage(), attachedPath);
		} catch (MailException | MessagingException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
