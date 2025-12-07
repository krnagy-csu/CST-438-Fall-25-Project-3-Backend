package com.example.CST438_P3.repo;

import com.example.CST438_P3.model.Message;
import com.example.CST438_P3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

   
    @Query("SELECT m FROM Message m " +
           "WHERE (m.sender.id = :user1 AND m.recipient.id = :user2) " +
           "   OR (m.sender.id = :user2 AND m.recipient.id = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<Message> getChatHistory(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

 
    List<Message> findByRecipientIdOrderByTimestampDesc(Long recipientId);
}