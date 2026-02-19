package com.ensureu.notification.service;

public interface EmailOperator {
    void send(String to, String subject, String body);
}
