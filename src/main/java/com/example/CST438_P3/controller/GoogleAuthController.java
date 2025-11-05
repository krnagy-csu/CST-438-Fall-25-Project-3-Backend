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
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    // Storage for device states
    private final Map<String, OAuthState> deviceStates = new ConcurrentHashMap<>();
    
    // NEW: Store deviceId by the URL that was requested
    // Key: the full OAuth URL, Value: deviceId
    private final Map<String, String> urlToDeviceId = new ConcurrentHashMap<>();

    @GetMapping("/start")
    public ResponseEntity<?> startGoogleAuth(@RequestParam String deviceId, HttpSession session) {
        // Mark device as waiting
        deviceStates.put(deviceId, new OAuthState("WAITING", null, null, null));
        
        // Store deviceId in session (this won't work but keep it as backup)
        session.setAttribute("deviceId", deviceId);
        
        // Build the OAuth URL
        String googleAuthUrl = backendUrl + "/oauth2/authorization/google?state=" + deviceId;
        
        // NEW: Store mapping from this URL pattern to deviceId
        // We'll use the deviceId as a key to look up later
        urlToDeviceId.put(deviceId, deviceId);
        
        System.out.println("Stored deviceId: " + deviceId);
        
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", state.getStatus());
        
        if ("SUCCESS".equals(state.getStatus())) {
            response.put("jwt", state.getJwt());
            response.put("user", state.getUser());
            deviceStates.remove(deviceId);
        } else if ("ERROR".equals(state.getStatus())) {
            response.put("error", state.getError());
            deviceStates.remove(deviceId);
        }
        
        return ResponseEntity.ok(response);
    }

    public void updateDeviceState(String deviceId, OAuthState state) {
        deviceStates.put(deviceId, state);
    }

    public Map<String, OAuthState> getDeviceStates() {
        return deviceStates;
    }
    
    // NEW: Method to find deviceId from session attributes
    public String findDeviceIdFromSession(HttpSession session) {
        if (session == null) return null;
        
        // Check direct attribute
        String deviceId = (String) session.getAttribute("deviceId");
        if (deviceId != null && deviceId.startsWith("device_")) {
            System.out.println("Found deviceId in session attribute: " + deviceId);
            return deviceId;
        }
        
        // Search through all stored deviceIds to see if any match
        // Look through authorization request attributes stored by Spring
        try {
            java.util.Enumeration<String> attrs = session.getAttributeNames();
            while (attrs.hasMoreElements()) {
                String attrName = attrs.nextElement();
                System.out.println("Session attribute: " + attrName);
                Object attrValue = session.getAttribute(attrName);
                
                if (attrValue != null) {
                    String str = attrValue.toString();
                    // Look for device_ pattern in any attribute
                    for (String storedDeviceId : urlToDeviceId.values()) {
                        if (str.contains(storedDeviceId)) {
                            System.out.println("Found deviceId in session data: " + storedDeviceId);
                            return storedDeviceId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching session: " + e.getMessage());
        }
        
        return null;
    }
}