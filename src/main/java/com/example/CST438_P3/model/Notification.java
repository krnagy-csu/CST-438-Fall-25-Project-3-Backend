package com.example.CST438_P3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="Notifications")
public class Notification {
    //Sender, recipient, body, related group (optional)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="recipient_id",nullable=false)
    private User recipient;

    @ManyToOne
    @JoinColumn(name="sender_id",nullable=true)
    private User sender;

    @ManyToOne
    @JoinColumn(name="group_id",nullable=true)
    private Group group;

    @Column(name="title",nullable=false)
    private String title;

    @Column(name="body")
    private String body;

    @Column(name="date")
    private LocalDateTime date;

    @Column(name="read")
    private boolean notifRead = false;

    public Notification() {}

    @PrePersist
    protected void onCreate() {
        this.date = LocalDateTime.now();
    }

    public Notification(User recipient, String title, String body) {
    this.recipient = recipient;
    this.title = title;
    this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getNotifRead() {
        return notifRead;
    }

    public void setNotifRead(boolean notifRead) {
        this.notifRead = notifRead;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    

}
