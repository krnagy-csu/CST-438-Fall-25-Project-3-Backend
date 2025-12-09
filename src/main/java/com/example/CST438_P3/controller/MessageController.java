package com.example.CST438_P3.controller;

import com.example.CST438_P3.model.Message;
import com.example.CST438_P3.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestParam Long senderId,
                               @RequestParam Long recipientId,
                               @RequestParam String body) {
        return messageService.sendMessage(senderId, recipientId, body);
    }

    @GetMapping("/inbox/{userId}")
    public List<Message> getInbox(@PathVariable Long userId) {
        return messageService.getInbox(userId);
    }

   
    @GetMapping("/chat")
    public List<Message> getChatHistory(@RequestParam Long user1Id,
                                        @RequestParam Long user2Id) {
        return messageService.getChatHistory(user1Id, user2Id);
    }

   
    @PutMapping("/{messageId}/read")
    public void markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
    }
}