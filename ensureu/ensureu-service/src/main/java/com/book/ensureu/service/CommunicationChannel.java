package com.book.ensureu.service;

import com.book.ensureu.dto.Message;

public abstract class CommunicationChannel {

	CommunicationChannelStrategy communicaChannel;
	Message message;

	public CommunicationChannel() {

	}

	public void doCommunicate() {
		communicaChannel.sendMessage(message);
	}

	public void setCommunicationChannel(CommunicationChannelStrategy communicationChannelStrategy) {
		this.communicaChannel = communicationChannelStrategy;

	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
