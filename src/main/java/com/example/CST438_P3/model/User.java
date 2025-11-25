package com.example.CST438_P3.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "zip_code")
    private String zipCode;

    @ManyToMany(mappedBy = "members")
    private Set<Group> groups = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    private Set<Group> createdGroups = new HashSet<>();
   
    public User() { }


    public User(String username, String email, String password, String zipCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.zipCode = zipCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Group> getCreatedGroups() {
        return createdGroups;
    }

    public void setCreatedGroups(Set<Group> createdGroups) {
        this.createdGroups = createdGroups;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
                + ", zipCode=" + zipCode + ", groups=" + groups + ", createdGroups=" + createdGroups + "]";
    }

    
}
