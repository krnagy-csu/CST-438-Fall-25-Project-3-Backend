
package com.example.CST438_P3.service;
import com.example.CST438_P3.model.Message;
import com.example.CST438_P3.model.User;
import com.example.CST438_P3.repo.MessageRepository;
import com.example.CST438_P3.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Send a new message
    public Message sendMessage(Long senderId, Long recipientId, String body) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            throw new IllegalArgumentException("Sender or recipient not found");
        }

        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setRecipient(recipientOpt.get());
        message.setBody(body);

        return messageRepository.save(message);
    }

    // Get inbox messages for a user
    public List<Message> getInbox(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return messageRepository.findByRecipientIdOrderByTimestampDesc(userId);
    }

    // Get chat history between two users
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        return messageRepository.getChatHistory(user1Id, user2Id);
    }

    // Mark message as read
    public void markAsRead(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }
}