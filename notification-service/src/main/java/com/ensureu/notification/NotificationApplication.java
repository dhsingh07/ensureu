package com.ensureu.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EnsureU Notification Service
 * A product of GrayscaleLabs AI Pvt Ltd.
 *
 * Handles Email, SMS, and Push notifications for the EnsureU platform.
 */
@SpringBootApplication
@EnableScheduling
@EnableMongoAuditing
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
