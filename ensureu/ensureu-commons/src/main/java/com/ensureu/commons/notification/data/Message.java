package com.ensureu.commons.notification.data;

public class Message<T> {
	private String subject;
	private T content;
	private Long createdDate;
	
	
	public Message() {
		super();
	}

	public Message(String subject, T content, Long createdDate) {
		super();
		this.subject = subject;
		this.content = content;
		this.createdDate = createdDate;
	}

	public String getSubject() {
		return subject;
	}

	public T getContent() {
		return content;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	@Override
	public String toString() {
		return "Message [subject=" + subject + ", content=" + content + ", createdDate=" + createdDate + "]";
	}
	
	
}
