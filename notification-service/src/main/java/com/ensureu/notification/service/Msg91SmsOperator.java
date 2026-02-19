package com.ensureu.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Msg91SmsOperator implements SmsOperator {

    private static final Logger log = LoggerFactory.getLogger(Msg91SmsOperator.class);

    @Value("${msg91.sender-id}")
    private String senderId;

    @Value("${msg91.auth-key}")
    private String authKey;

    @Override
    public void send(String to, String message) {
        log.info("MSG91 SMS: to={}, senderId={}, message={}", to, senderId, message);
        // TODO: Implement actual MSG91 API call
    }
}
