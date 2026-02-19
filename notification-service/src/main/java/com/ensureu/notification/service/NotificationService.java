package com.ensureu.notification.service;

import org.springframework.stereotype.Service;

import com.ensureu.notification.domain.Notification;
import com.ensureu.notification.domain.NotificationChannel;
import com.ensureu.notification.domain.NotificationStatus;
import com.ensureu.notification.dto.NotificationRequest;
import com.ensureu.notification.dto.NotificationResponse;
import com.ensureu.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailOperator emailOperator;
    private final SmsOperator smsOperator;

    public NotificationService(NotificationRepository notificationRepository,
                               EmailOperator emailOperator,
                               SmsOperator smsOperator) {
        this.notificationRepository = notificationRepository;
        this.emailOperator = emailOperator;
        this.smsOperator = smsOperator;
    }

    public NotificationResponse send(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setChannel(request.getChannel());
        notification.setRecipient(request.getRecipient());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);

        notification = notificationRepository.save(notification);

        try {
            if (request.getChannel() == NotificationChannel.EMAIL) {
                emailOperator.send(request.getRecipient(), request.getSubject(), request.getMessage());
            } else if (request.getChannel() == NotificationChannel.SMS) {
                smsOperator.send(request.getRecipient(), request.getMessage());
            }
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setError(e.getMessage());
        }

        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setChannel(notification.getChannel());
        response.setStatus(notification.getStatus());
        response.setRecipient(notification.getRecipient());
        response.setMessage(notification.getMessage());
        return response;
    }
}
