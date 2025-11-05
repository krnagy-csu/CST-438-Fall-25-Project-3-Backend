package com.example.CST438_P3.controller;

import com.example.CST438_P3.model.OAuthState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth/google")
@CrossOrigin(origins = "*") // Adjust for production
public class GoogleAuthController {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    // Temporary storage (use Redis in production)
    private final Map<String, OAuthState> deviceStates = new ConcurrentHashMap<>();

    @GetMapping("/start")
    public ResponseEntity<?> startGoogleAuth(@RequestParam String deviceId, HttpSession session) {
        // Mark device as waiting
        deviceStates.put(deviceId, new OAuthState("WAITING", null, null, null));

        // Store deviceId in session so we can retrieve it after OAuth callback
        session.setAttribute("deviceId", deviceId);
        
        // Return Google OAuth URL with state parameter
        String googleAuthUrl = backendUrl + "/oauth2/authorization/google?state=" + deviceId;
        
        Map<String, String> response = new HashMap<>();
        response.put("url", googleAuthUrl);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam String deviceId) {
        OAuthState state = deviceStates.get(deviceId);
        
        if (state == null) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "UNKNOWN");
            return ResponseEntity.ok(response);
        }
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("status", state.getStatus());
        
        if ("SUCCESS".equals(state.getStatus())) {
            response.put("jwt", state.getJwt());
            response.put("user", state.getUser());
            
            // Clean up after successful retrieval
            deviceStates.remove(deviceId);
        } else if ("ERROR".equals(state.getStatus())) {
            response.put("error", state.getError());
            deviceStates.remove(deviceId);
        }
        
        return ResponseEntity.ok(response);
    }

    // Internal method to update state (called by OAuth success handler)
    public void updateDeviceState(String deviceId, OAuthState state) {
        deviceStates.put(deviceId, state);
    }

    public Map<String, OAuthState> getDeviceStates() {
        return deviceStates;
    }
}
