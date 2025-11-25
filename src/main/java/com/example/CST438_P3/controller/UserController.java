package com.example.CST438_P3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import com.example.CST438_P3.model.User;
import com.example.CST438_P3.repo.UserRepository;

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
        return userRepository.findAll();
    }

    @GetMapping("/api/users/me")
    public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            throw new RuntimeException("No OAuth user found.");
        }

        // Try both keys just in case
        String email = principal.getAttribute("email");
        String email2 = principal.getAttribute("emailAddress");

        final String resolvedEmail = (email != null) ? email : email2;
        if (resolvedEmail == null) {
            throw new RuntimeException("Email not provided by OAuth provider");
        }

        return userRepository.findByEmail(resolvedEmail).orElseGet(() -> {
            String name = principal.getAttribute("name");

            User newUser = new User();
            newUser.setEmail(resolvedEmail);
            newUser.setUsername(name != null ? name : resolvedEmail);
            newUser.setPassword("OAUTH_USER");
            newUser.setZipCode(null);

            return userRepository.save(newUser);
        });
    }
}
