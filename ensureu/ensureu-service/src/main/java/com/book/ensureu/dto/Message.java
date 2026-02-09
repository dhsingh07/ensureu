package com.book.ensureu.dto;

public class Message {
	private String message;
	private String subject;
	public Message(String message, String subject) {
		super();
		this.message = message;
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public String getSubject() {
		return subject;
	}
	
	
}
