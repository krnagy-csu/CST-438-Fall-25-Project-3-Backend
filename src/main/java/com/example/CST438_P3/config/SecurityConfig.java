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
        
        // Get the original request URL to find deviceId
        String referer = request.getHeader("Referer");
        String deviceId = null;
        
        // Try to extract deviceId from referer URL
        if (referer != null && referer.contains("state=device_")) {
            int startIndex = referer.indexOf("state=device_");
            if (startIndex != -1) {
                int endIndex = referer.indexOf("&", startIndex);
                if (endIndex == -1) endIndex = referer.length();
                deviceId = referer.substring(startIndex + 6, endIndex); // "state=".length() = 6
            }
        }
        
        // Also check session as fallback
        if (deviceId == null) {
            deviceId = (String) request.getSession().getAttribute("deviceId");
        }
        
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
                
                // Clear session
                request.getSession().removeAttribute("deviceId");
                
                // Show success page
                response.setContentType("text/html");
                response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><title>Success</title></head>" +
                    "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                    "<h2 style='color: green;'>✅ Success!</h2>" +
                    "<p>You have successfully signed in as " + email + "</p>" +
                    "<p>DeviceID: " + deviceId + "</p>" +
                    "<p>You can close this window and return to the app.</p>" +
                    "<script>setTimeout(() => window.close(), 2000);</script>" +
                    "</body>" +
                    "</html>"
                );
                return; // Important: return here to prevent redirect
            } catch (Exception e) {
                OAuthState errorState = new OAuthState("ERROR", null, null, "Failed to generate token: " + e.getMessage());
                googleAuthController.updateDeviceState(deviceId, errorState);
                
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
        
        // Web browser - redirect to home
        response.sendRedirect("/home");
    };
}
}
    