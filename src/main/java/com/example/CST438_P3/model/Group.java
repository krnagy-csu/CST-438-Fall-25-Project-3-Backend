package com.example.CST438_P3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;


    //Is effectively another table linking users and groups, if we need more info
    //we can create group_members entity later to deal with that
    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private Set<User> members = new HashSet<>();

   

    public Group() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Group(String name, String description, String activityType, String zipCode, User creator) {
        this();
        this.name = name;
        this.description = description;
        this.activityType = activityType;
        this.zipCode = zipCode;
        this.creator = creator;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.getGroups().remove(this);
    }

    public int getCurrentMemberCount() {
        return this.members.size();
    }

    @Override
    public String toString()
    {
        return "Group [id=" + id + ", name=" + name + ", description=" + description + ", activityType=" + activityType
                + ", zipCode=" + zipCode + ", maxMembers=" + maxMembers + ", eventDate=" + eventDate
                + ", isRecurring=" + isRecurring + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", creator=" + creator.getUsername() + ", membersCount=" + members.size() + "]";
    }
}
