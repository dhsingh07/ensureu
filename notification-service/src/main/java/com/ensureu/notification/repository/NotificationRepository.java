package com.ensureu.notification.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ensureu.notification.domain.Notification;
import com.ensureu.notification.domain.NotificationStatus;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status, Pageable pageable);
}
