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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for API
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login**", "/error", "/auth/**", "/oauth2/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(mobileOAuth2SuccessHandler())
            );
        
        return http.build();
    }
    @Bean
public AuthenticationSuccessHandler mobileOAuth2SuccessHandler() {
    return (request, response, authentication) -> {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        // Try multiple ways to get deviceId
        String deviceId = null;
        
        // 1. Check session
        HttpSession session = request.getSession(false);
        if (session != null) {
            deviceId = (String) session.getAttribute("deviceId");
        }
        
        // 2. Check if Spring's state contains our deviceId info
        // Look at the authorization request attributes stored by Spring Security
        if (deviceId == null) {
            try {
                // Get all session attributes and look for deviceId
                if (session != null) {
                    java.util.Enumeration<String> attrs = session.getAttributeNames();
                    while (attrs.hasMoreElements()) {
                        String attrName = attrs.nextElement();
                        Object attrValue = session.getAttribute(attrName);
                        // Look through all session attributes for our deviceId
                        if (attrValue != null && attrValue.toString().contains("device_")) {
                            String str = attrValue.toString();
                            int idx = str.indexOf("device_");
                            if (idx >= 0) {
                                // Extract device_XXXXX
                                String potential = str.substring(idx);
                                int endIdx = potential.indexOf(" ");
                                if (endIdx < 0) endIdx = potential.indexOf("}");
                                if (endIdx < 0) endIdx = potential.indexOf(",");
                                if (endIdx > 0) {
                                    deviceId = potential.substring(0, endIdx);
                                } else {
                                    deviceId = potential;
                                }
                                if (deviceId.startsWith("device_")) break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        
        // Log for debugging
        System.out.println("=== OAuth Success Handler ===");
        System.out.println("Email: " + email);
        System.out.println("Found deviceId: " + deviceId);
        
        if (deviceId != null && deviceId.startsWith("device_")) {
            // Mobile app - store JWT for polling
            try {
                String jwt = jwtTokenProvider.generateToken(email);
                
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("email", email);
                userMap.put("name", name);
                userMap.put("picture", picture);
                userMap.put("id", email);
                
                OAuthState successState = new OAuthState("SUCCESS", jwt, userMap, null);
                googleAuthController.updateDeviceState(deviceId, successState);
                
                System.out.println("Stored SUCCESS state for deviceId: " + deviceId);
                
                // Clear session
                if (session != null) {
                    session.removeAttribute("deviceId");
                }
                
                // Show success page
                response.setContentType("text/html");
                response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><title>Success</title></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h2 style='color: green;'>✅ Success!</h2>" +
                    "<p>You have successfully signed in as " + email + "</p>" +
                    "<p><strong>You can close this window and return to the app.</strong></p>" +
                    "<script>setTimeout(() => { window.close(); }, 2000);</script>" +
                    "</body>" +
                    "</html>"
                );
                return;
            } catch (Exception e) {
                System.out.println("Error in success handler: " + e.getMessage());
                e.printStackTrace();
                
                OAuthState errorState = new OAuthState("ERROR", null, null, "Failed to generate token: " + e.getMessage());
                if (deviceId != null) {
                    googleAuthController.updateDeviceState(deviceId, errorState);
                }
                
                response.setContentType("text/html");
                response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><title>Error</title></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h2 style='color: red;'>❌ Error</h2>" +
                    "<p>Authentication failed. Please try again.</p>" +
                    "<p>Error: " + e.getMessage() + "</p>" +
                    "</body>" +
                    "</html>"
                );
                return;
            }
        }
        
        System.out.println("No valid deviceId found, redirecting to /home");
        // Web browser - redirect to home
        response.sendRedirect("/home");
    };
}
}