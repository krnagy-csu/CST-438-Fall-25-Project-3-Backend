package com.example.CST438_P3.controller;

import com.example.CST438_P3.model.OAuthState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth/google")
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    private final Map<String, OAuthState> deviceStates = new ConcurrentHashMap<>();

    @GetMapping("/start")
    public ResponseEntity<?> startGoogleAuth() {
        // Clear any old "latest" state
        deviceStates.remove("latest");
        deviceStates.put("latest", new OAuthState("WAITING", null, null, null));
        
        String googleAuthUrl = "https://cst438-p3-backend-de9dd99b3c9a.herokuapp.com/oauth2/authorization/google";        
        System.out.println("Generated OAuth URL: " + googleAuthUrl);
        
        Map<String, String> response = new HashMap<>();
        response.put("url", googleAuthUrl);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/github/start")
    public ResponseEntity<?> startGithubAuth() {
        // Reset latest state just like Google
        deviceStates.remove("latest");
        deviceStates.put("latest", new OAuthState("WAITING", null, null, null));

        // Build the GitHub auth URL (use backendUrl like Google ideally)
        String githubAuthUrl = backendUrl + "/oauth2/authorization/github";
        System.out.println("Generated GitHub OAuth URL: " + githubAuthUrl);

        Map<String, String> response = new HashMap<>();
        response.put("url", githubAuthUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkStatus() {
        // Always check the "latest" login
        OAuthState state = deviceStates.get("latest");
        
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
            // Don't remove it immediately - let the frontend grab it
        } else if ("ERROR".equals(state.getStatus())) {
            response.put("error", state.getError());
        }
        
        return ResponseEntity.ok(response);
    }

    public void updateDeviceState(String deviceId, OAuthState state) {
        deviceStates.put(deviceId, state);
    }
}