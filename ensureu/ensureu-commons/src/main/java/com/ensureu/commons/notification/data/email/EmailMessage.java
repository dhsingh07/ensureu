package com.ensureu.commons.notification.data.email;

import com.ensureu.commons.constant.EmailType;
import com.ensureu.commons.notification.data.Message;

public class EmailMessage<T> extends Message<T> {

	private EmailType emailType;
	private boolean read;
	private boolean deleted;
	private String createdBy;

	public EmailMessage(String subject, T content, Long createdDate) {
		super(subject, content, createdDate);
	}

	
	public EmailType getEmailType() {
		return emailType;
	}

	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	@Override
	public String toString() {
		return "EmailMessage [emailType=" + emailType + ", read=" + read + ", deleted=" + deleted + ", createdBy="
				+ createdBy + "]";
	}
	
	
}


