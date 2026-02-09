package com.book.ensureu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.book.ensureu.service.CommunicationChannel;
import com.book.ensureu.service.CommunicationChannelStrategy;

@Service
public class CommunicationChannelImpl extends CommunicationChannel {

	@Autowired
	@Qualifier("mobileCommunication")
	CommunicationChannelStrategy mobileCommunicationChannel;

	@Autowired
	@Qualifier("emailCommunication")
	CommunicationChannelStrategy emailCommunicationChannel;

	public CommunicationChannelImpl() {
	}

	
	public CommunicationChannelStrategy getMobileCommunicationChannel() {
		return mobileCommunicationChannel;
	}


	public CommunicationChannelStrategy getEmailCommunicationChannel() {
		return emailCommunicationChannel;
	}


	public void display() {
		System.out.println("Display action.....");
	}
}
