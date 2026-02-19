package com.ensureu.notification.service;

public interface SmsOperator {
    void send(String to, String message);
}
