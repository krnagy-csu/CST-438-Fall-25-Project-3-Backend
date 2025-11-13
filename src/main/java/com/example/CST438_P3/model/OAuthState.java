package com.example.CST438_P3.model;

import java.util.Map;

public class OAuthState {
    private String status; // "WAITING", "SUCCESS", "ERROR"
    private String jwt;
    private Map<String, Object> user;
    private String error;

    public OAuthState() {}

    public OAuthState(String status, String jwt, Map<String, Object> user, String error) {
        this.status = status;
        this.jwt = jwt;
        this.user = user;
        this.error = error;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }
    
    public Map<String, Object> getUser() { return user; }
    public void setUser(Map<String, Object> user) { this.user = user; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
