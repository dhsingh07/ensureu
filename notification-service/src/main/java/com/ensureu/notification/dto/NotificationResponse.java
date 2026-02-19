package com.ensureu.notification.dto;

import com.ensureu.notification.domain.NotificationChannel;
import com.ensureu.notification.domain.NotificationStatus;

public class NotificationResponse {
    private String id;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String recipient;
    private String message;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
