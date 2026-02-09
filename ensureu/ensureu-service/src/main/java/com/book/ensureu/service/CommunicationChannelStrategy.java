package com.book.ensureu.service;

import com.book.ensureu.dto.Message;

@FunctionalInterface
public interface CommunicationChannelStrategy {
	public void sendMessage(Message message);
}
