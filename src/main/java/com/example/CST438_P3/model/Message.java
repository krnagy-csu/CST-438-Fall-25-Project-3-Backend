package com.example.CST438_P3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private String body;
    
    private LocalDateTime timestamp;

    private boolean isRead = false;

    public Message() {}

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() 
    { 
        return id; 
    }

    public User getSender() 
    {
        return sender; 
    }
    public void setSender(User sender) 
    { 
        this.sender = sender;
    }

    public User getRecipient() 
    { 
        return recipient; 
    
    }
    public void setRecipient(User recipient) 
    { 
        this.recipient = recipient; 
    }

    public String getBody() 
    { 
        return body; 
    }


    public void setBody(String body) 
    { 
        this.body = body; 
    }

    public LocalDateTime getTimestamp() 
    { 
        return timestamp; 
    
    }
    public void setTimestamp(LocalDateTime timestamp) 
    { 
        this.timestamp = timestamp; 
    }

    public boolean getIsRead() 
         { 
            return isRead;
         }

    public void setIsRead(boolean read) 
    { 
        isRead = read; 
    }
}