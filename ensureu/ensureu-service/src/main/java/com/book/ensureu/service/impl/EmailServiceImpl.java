package com.book.ensureu.service.impl;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.book.ensureu.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	JavaMailSender javaMailSender;

	@Override
	public void sendSimpleMessage(String to, String subject, String messageBody) {

		LOGGER.info("sendSimpleMessage mail to " + to);
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(to);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(messageBody);
		javaMailSender.send(simpleMailMessage);

	}

	@Override
	public void sendMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String ... templateArgs) {

		LOGGER.info("sendMessageUsingTemplate mail to " + to);
		String text = String.format(template.getText(), templateArgs);
		sendSimpleMessage(to, subject, text);

	}

	@Override
	public void sendMessageWithAttachedment(String to, String subject, String messageBody, String attachedPath)
			throws MessagingException, MailException {

		LOGGER.info("sendMessageWithAttachedment mail to " + to);
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(messageBody);
			FileSystemResource fileSystem = new FileSystemResource(new File(attachedPath));
			mimeMessageHelper.addAttachment("Paper-Image", fileSystem);
			javaMailSender.send(mimeMessage);

		} catch (MessagingException | MailException e) {
			throw e;
		}

	}
	
	@Override
	public SimpleMailMessage getSimpleMailTemplateMessage() {
		SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
		simpleMailMessage.setText("Welcome on EnsureU services :\\n%s\\n");
		return simpleMailMessage;
	}

}
