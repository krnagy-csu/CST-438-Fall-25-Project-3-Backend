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
import com.example.CST438_P3.repo.UserRepository;
import com.example.CST438_P3.model.User;

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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login**", "/error",
                                "/auth/**", "/oauth2/**", "/api/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(mobileOAuth2SuccessHandler()));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler mobileOAuth2SuccessHandler() {
        return (request, response, authentication) -> {

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // Read deviceId from OAuth2 "state" parameter
            String deviceId = request.getParameter("state");

            if (deviceId == null || deviceId.isEmpty()) {
                System.out.println("⚠️ WARNING: state (deviceId) missing. Using fallback 'latest'.");
                deviceId = "latest";
            }

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            try {
                // Find or create user
                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            String username = generateUsernameFromEmail(email);
                            User newUser = new User(username, email, "OAUTH_USER", null);
                            return userRepository.save(newUser);
                        });

                // Generate JWT
                String jwt = jwtTokenProvider.generateToken(email);

                // Build user map for frontend
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("email", user.getEmail());
                userMap.put("username", user.getUsername());
                userMap.put("name", name);
                userMap.put("picture", picture);

                // Update correct device so polling works
                OAuthState successState = new OAuthState("SUCCESS", jwt, userMap, null);
                googleAuthController.updateDeviceState(deviceId, successState);

                System.out.println("✅ OAuth SUCCESS stored for deviceId: " + deviceId);

                // Show success page
                response.setContentType("text/html");
                response.getWriter().write(
                        "<!DOCTYPE html>" +
                                "<html><head><title>Success</title>" +
                                "<meta http-equiv='refresh' content='2;url=about:blank'>" +
                                "</head>" +
                                "<body style='font-family: Arial; text-align:center; padding:40px;'>" +
                                "<h2 style='color:green;'>Login Successful</h2>" +
                                "<p>You may now close this window.</p>" +
                                "<script>setTimeout(()=>{window.close();},1500);</script>" +
                                "</body></html>"
                );

            } catch (Exception e) {
                System.out.println("❌ OAuth Error: " + e.getMessage());
                response.sendRedirect("/error");
            }
        };
    }
}
