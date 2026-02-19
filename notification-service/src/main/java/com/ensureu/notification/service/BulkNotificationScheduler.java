package com.ensureu.notification.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ensureu.notification.domain.Notification;
import com.ensureu.notification.domain.NotificationChannel;
import com.ensureu.notification.domain.NotificationStatus;
import com.ensureu.notification.repository.NotificationRepository;

@Service
public class BulkNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BulkNotificationScheduler.class);

    private final NotificationRepository notificationRepository;
    private final EmailOperator emailOperator;
    private final SmsOperator smsOperator;

    @Value("${notification.bulk.batch-size:50}")
    private int batchSize;

    public BulkNotificationScheduler(NotificationRepository notificationRepository,
                                      EmailOperator emailOperator,
                                      SmsOperator smsOperator) {
        this.notificationRepository = notificationRepository;
        this.emailOperator = emailOperator;
        this.smsOperator = smsOperator;
    }

    @Scheduled(fixedDelayString = "${notification.bulk.fixed-delay-ms:60000}")
    public void processPendingNotifications() {
        List<Notification> pending = notificationRepository
                .findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING, PageRequest.of(0, batchSize));

        for (Notification notification : pending) {
            try {
                if (notification.getChannel() == NotificationChannel.EMAIL) {
                    emailOperator.send(notification.getRecipient(), notification.getSubject(), notification.getMessage());
                } else if (notification.getChannel() == NotificationChannel.SMS) {
                    smsOperator.send(notification.getRecipient(), notification.getMessage());
                }
                notification.setStatus(NotificationStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
                notification.setStatus(NotificationStatus.FAILED);
                notification.setError(e.getMessage());
            }
            notificationRepository.save(notification);
        }

        if (!pending.isEmpty()) {
            log.info("Processed {} pending notifications", pending.size());
        }
    }
}
