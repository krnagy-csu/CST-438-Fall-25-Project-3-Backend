package com.example.CST438_P3.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
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

    if (principal == null) {
        throw new RuntimeException("No OAuth user found");
    }

    String email = principal.getAttribute("email");
    if (email == null) {
        email = principal.getAttribute("emailAddress");
    }
    if (email == null) {
        throw new RuntimeException("Email not provided by OAuth provider");
    }

    //if user exists
    return userRepository.findByEmail(email)
            .orElseGet(() -> {
                //if not, create a new user automatically
                User newUser = new User();
                newUser.setEmail(email);
                String name = principal.getAttribute("name");
                newUser.setUsername(name != null ? name : email);

                newUser.setPassword("OAUTH_USER");
                newUser.setZipCode(null);

                return userRepository.save(newUser);
            });
}


}
