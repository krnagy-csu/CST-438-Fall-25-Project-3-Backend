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

import java.util.Base64;
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
        return email != null ? email.split("@")[0] : null;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/error",
                                "/auth/**", "/oauth2/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth.successHandler(mobileOAuth2SuccessHandler()));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler mobileOAuth2SuccessHandler() {
        return (request, response, authentication) -> {

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        
            String encoded = request.getParameter("state");
            String deviceId;

            try {
                deviceId = new String(Base64.getDecoder().decode(encoded));
            } catch (Exception e) {
                System.out.println("⚠️ Invalid Base64 state, fallback to raw");
                deviceId = encoded;
            }

            System.out.println("📌 OAuth callback for deviceId = " + deviceId);

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");

            try {
                // Find or create user
                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            String username = generateUsernameFromEmail(email);
                            return userRepository.save(new User(username, email, "OAUTH_USER", null));
                        });

                // Generate JWT
                String jwt = jwtTokenProvider.generateToken(email);

                // Build user map
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("email", user.getEmail());
                userMap.put("username", user.getUsername());
                userMap.put("name", name);
                userMap.put("picture", picture);

                // Store success state
                OAuthState success = new OAuthState("SUCCESS", jwt, userMap, null);
                googleAuthController.updateDeviceState(deviceId, success);

                System.out.println("✅ OAuth SUCCESS stored for deviceId: " + deviceId);

                // Success page
                response.setContentType("text/html");
                response.getWriter().write(
                        "<html><body style='text-align:center;padding:50px;font-family:Arial'>" +
                                "<h2 style='color:green;'>Success!</h2>" +
                                "<p>You can close this window.</p>" +
                                "<script>setTimeout(()=>{window.close();},1500);</script>" +
                                "</body></html>"
                );

            } catch (Exception e) {
                System.out.println("❌ OAuth error: " + e.getMessage());
                response.sendRedirect("/error");
            }
        };
    }
}
