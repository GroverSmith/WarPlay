package com.warplay.campaign.controller;

import com.warplay.campaign.entity.User;
import com.warplay.campaign.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Login/Register endpoint for Google OAuth
    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest request) {

        // Validate required fields
        if (request.getGoogleId() == null || request.getEmail() == null || request.getName() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if user already exists by Google ID
        Optional<User> existingUser = userRepository.findByGoogleId(request.getGoogleId());

        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            user.setName(request.getName()); // Update name in case it changed
            user.setEmail(request.getEmail()); // Update email in case it changed
            user.setProfilePictureUrl(request.getProfilePictureUrl());
            user.updateLastLogin();
        } else {
            // Check if user exists with same email but no Google ID (shouldn't happen with OAuth)
            Optional<User> emailUser = userRepository.findByEmail(request.getEmail());
            if (emailUser.isPresent()) {
                // Link Google ID to existing email user
                user = emailUser.get();
                user.setGoogleId(request.getGoogleId());
                user.setName(request.getName());
                user.setProfilePictureUrl(request.getProfilePictureUrl());
                user.updateLastLogin();
            } else {
                // Create completely new user
                user = new User(request.getGoogleId(), request.getEmail(), request.getName(), request.getProfilePictureUrl());
                isNewUser = true;
            }
        }

        // Save user
        User savedUser = userRepository.save(user);

        // Return response
        AuthResponse response = new AuthResponse();
        response.setUser(savedUser);
        response.setIsNewUser(isNewUser);
        response.setMessage(isNewUser ? "User registered successfully" : "User logged in successfully");

        return ResponseEntity.ok(response);
    }

    // Logout endpoint (optional - mainly for frontend state management)
    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        return ResponseEntity.ok(new Object() {
            public final String message = "Logged out successfully";
        });
    }

    // Get current user info (for checking if still logged in)
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestParam String googleId) {
        Optional<User> user = userRepository.findByGoogleId(googleId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Request classes
    public static class GoogleLoginRequest {
        private String googleId;
        private String email;
        private String name;
        private String profilePictureUrl;

        // Getters and setters
        public String getGoogleId() { return googleId; }
        public void setGoogleId(String googleId) { this.googleId = googleId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getProfilePictureUrl() { return profilePictureUrl; }
        public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    }

    // Response class
    public static class AuthResponse {
        private User user;
        private Boolean isNewUser;
        private String message;

        // Getters and setters
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public Boolean getIsNewUser() { return isNewUser; }
        public void setIsNewUser(Boolean isNewUser) { this.isNewUser = isNewUser; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}