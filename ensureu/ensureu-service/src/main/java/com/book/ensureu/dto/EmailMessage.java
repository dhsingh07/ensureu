package com.book.ensureu.dto;

public class EmailMessage extends Message {

	private String to;
	private String from;
	
	public EmailMessage(String message, String subject) {
		super(message, subject);
	}
	
	public EmailMessage(String message, String subject, String to) {
		super(message, subject);
		this.to = to;
	}

	public EmailMessage(String message, String subject, String to, String from) {
		super(message, subject);
		this.to = to;
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	
}
