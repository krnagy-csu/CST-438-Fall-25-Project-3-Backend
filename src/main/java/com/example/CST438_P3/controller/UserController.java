package com.example.CST438_P3.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.CST438_P3.security.JwtTokenProvider;  

import java.util.List;
import com.example.CST438_P3.model.User;     
import com.example.CST438_P3.repo.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.example.CST438_P3.repo.UserRepository;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
private JwtTokenProvider jwtTokenProvider;


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

@GetMapping("/api/users/me-jwt-test")
public User getCurrentUserJwtTest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Missing or invalid Authorization header"
        );
    }

    String token = authHeader.substring(7);

    // ⚠️ Use your real method name here:
    // e.g. jwtTokenProvider.getUsernameFromToken(token) or getUsernameFromJWT(token)
    String email = jwtTokenProvider.getEmailFromToken(token);


    if (email == null || email.isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Invalid token");
    }

    System.out.println("JWT /me test email = " + email);

    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not found: " + email
        ));
}
}
