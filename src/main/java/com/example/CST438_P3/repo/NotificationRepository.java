package com.example.CST438_P3.repo;

import com.example.CST438_P3.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //cursed names woo
    List<Notification> findByRecipientIdOrderByDateDesc(Long recipientId);
    List<Notification> findByRecipientIdAndNotifReadFalseOrderByDateDesc(Long recipientId);

    Long countByRecipientIdAndNotifReadFalse(Long recipientId);
}
