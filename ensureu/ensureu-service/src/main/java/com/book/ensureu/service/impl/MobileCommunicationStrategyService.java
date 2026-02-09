package com.book.ensureu.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.book.ensureu.dto.Message;
import com.book.ensureu.service.CommunicationChannelStrategy;

@Service
@Qualifier("mobileCommunication")
public class MobileCommunicationStrategyService implements CommunicationChannelStrategy{

	@Override
	public void sendMessage(Message message) {
		System.out.println("MobileCommunicationStrategyService message....");
	}

}
