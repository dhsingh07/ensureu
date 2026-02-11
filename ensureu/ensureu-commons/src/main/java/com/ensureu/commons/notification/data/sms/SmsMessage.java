package com.ensureu.commons.notification.data.sms;

import com.ensureu.commons.notification.data.Message;

public class SmsMessage<T> extends Message<T> {

	public SmsMessage(String subject, T content, Long createdDate) {
		super(subject, content, createdDate);
	}

}
