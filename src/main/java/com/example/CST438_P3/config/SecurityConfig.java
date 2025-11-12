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
                .requestMatchers("/", "/login**", "/error", "/auth/**", "/oauth2/**",  "/api/**").permitAll()
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
        
        System.out.println("=== OAuth Success ===");
        System.out.println("Email: " + email);
        
        try {
            // Generate JWT
            String jwt = jwtTokenProvider.generateToken(email);
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("email", email);
            userMap.put("name", name);
            userMap.put("picture", picture);
            userMap.put("id", email);
            
            OAuthState successState = new OAuthState("SUCCESS", jwt, userMap, null);
            
            // Store with a special "latest" key that the frontend will poll
            googleAuthController.updateDeviceState("latest", successState);
            
            System.out.println("✅ Stored SUCCESS state");
            
            // Show success page
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