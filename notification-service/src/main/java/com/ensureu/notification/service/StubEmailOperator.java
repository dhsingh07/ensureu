package com.ensureu.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StubEmailOperator implements EmailOperator {

    private static final Logger log = LoggerFactory.getLogger(StubEmailOperator.class);

    @Override
    public void send(String to, String subject, String body) {
        log.info("STUB EMAIL: to={}, subject={}, body={}", to, subject, body);
    }
}
