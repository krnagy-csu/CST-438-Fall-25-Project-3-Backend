package com.example.CST438_P3.config;

import com.example.CST438_P3.controller.GoogleAuthController;
import com.example.CST438_P3.model.OAuthState;
import com.example.CST438_P3.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;


import com.example.CST438_P3.repo.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.example.CST438_P3.model.User;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private GoogleAuthController googleAuthController;

    @Autowired
    private UserRepository userRepository;

    private String generateUsernameFromEmail(String email) {
        if (email == null) return null;
        return email.split("@")[0];
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disable for API
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login**", "/error", "/auth/**", "/oauth2/**",  "/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(mobileOAuth2SuccessHandler())
            );
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow frontend origin (localhost:8081 for now)
        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        
        // Allow standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow headers (Authorization is crucial for your JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Allow credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
public AuthenticationSuccessHandler mobileOAuth2SuccessHandler() {
    return (request, response, authentication) -> {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // Figure out which provider we’re using: "google" or "github"
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // "google" or "github"

        Map<String, Object> attrs = oAuth2User.getAttributes();

        String email = null;
        String name = null;
        String picture = null;

        if ("google".equals(registrationId)) {
            // Google attribute names
            email = (String) attrs.get("email");
            name = (String) attrs.get("name");
            picture = (String) attrs.get("picture");
        } else if ("github".equals(registrationId)) {
            // GitHub user JSON: login, id, avatar_url, name, email (email might be null)
            String login = (String) attrs.get("login");
            email = (String) attrs.get("email");

            // Fallback if GitHub email is private
            if (email == null && login != null) {
                email = login + "@users.noreply.github.com";
            }

            name = (String) attrs.getOrDefault("name", login);
            picture = (String) attrs.get("avatar_url");
        }

        System.out.println("=== OAuth Success (" + registrationId + ") ===");
        System.out.println("Email: " + email);

        try {
             // Make email effectively final for lambda usage
    final String finalEmail = email;

    // 1) Find or create the User in DB
    User user = userRepository.findByEmail(finalEmail)
            .orElseGet(() -> {
                String username = generateUsernameFromEmail(finalEmail);

                User newUser = new User(
                        username,          // username
                        finalEmail,        // email
                        "OAUTH_USER",      // placeholder password
                        null               // zipCode
                );
                return userRepository.save(newUser);
            });

            // 2) Generate JWT
            String jwt = jwtTokenProvider.generateToken(email);

            // 3) Build the user map that goes back to the app
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("username", user.getUsername());
            userMap.put("name", name);
            userMap.put("picture", picture);
            userMap.put("provider", registrationId);

            OAuthState successState = new OAuthState("SUCCESS", jwt, userMap, null);

            // Store with the "latest" key for the app to poll
            googleAuthController.updateDeviceState("latest", successState);

            System.out.println("✅ Stored SUCCESS state");

            // Show success page in browser
            response.setContentType("text/html");
            response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<title>Success</title>" +
                    "<meta http-equiv='refresh' content='2;url=about:blank'>" +
                    "</head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h2 style='color: green;'>✅ Success!</h2>" +
                    "<p>You have successfully signed in as " + email + "</p>" +
                    "<p><strong>You can close this window and return to the app.</strong></p>" +
                    "<p style='color: gray; font-size: 12px;'>This window will close automatically...</p>" +
                    "<script>" +
                    "setTimeout(() => { " +
                    "  try { window.close(); } catch(e) { " +
                    "    document.body.innerHTML = '<h2>Please close this window manually</h2>'; " +
                    "  }" +
                    "}, 2000);" +
                    "</script>" +
                    "</body>" +
                    "</html>"
            );

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/home");
        }
    };
}
}