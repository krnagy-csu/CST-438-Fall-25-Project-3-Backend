package com.example.CST438_P3.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.example.CST438_P3.model.User;     
import com.example.CST438_P3.repo.UserRepository;

import com.example.CST438_P3.repo.UserRepository;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/home")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Map.of(
            "name", principal.getAttribute("name"),
            "email", principal.getAttribute("email"),
            "picture", principal.getAttribute("picture")
        );
    }
    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();  // ← This shows H2 or Postgres users
    }
    @GetMapping("/api/users/me")
public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
    String email = principal.getAttribute("email");
    return userRepository.findByEmail(email).orElseThrow();
}

@GetMapping("/api/users/username/{username}")
public User getUserByUsername(@PathVariable String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
}
}
