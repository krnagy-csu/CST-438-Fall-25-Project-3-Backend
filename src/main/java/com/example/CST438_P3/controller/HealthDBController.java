package com.example.CST438_P3.controller;

import com.example.CST438_P3.repo.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HealthDBController {
    
@Autowired
private UserRepository userRepository;
    
@GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "message", "Backend is running successfully",
            "timestamp", System.currentTimeMillis()
        );
    }

    // DB health check
    @GetMapping("/api/health/db")
    public Map<String, Object> dbHealth() {
        try {
            long userCount = userRepository.count();  // 🔍 simple query

            return Map.of(
                "status", "UP",
                "db", "CONNECTED",
                "userCount", userCount,
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "DOWN",
                "db", "ERROR",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
}
    

