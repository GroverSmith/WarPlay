package com.warplay.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.warplay.entity.User;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/google-signin")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> payload) {
        String idTokenString = payload.get("idToken");

        logger.info("user sign-in attempt with payload: {}", payload);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("954824091811-cmev31d6ed8t005e09fl7njbijc8ujoq.apps.googleusercontent.com"))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            logger.info("verifier returned token {}", idToken);
            if (idToken != null) {
                GoogleIdToken.Payload tokenPayload = idToken.getPayload();
                String email = tokenPayload.getEmail();
                String name = (String) tokenPayload.get("name");
                String googleId = tokenPayload.getSubject();
                
                // Note: Google JWT often doesn't include profile picture
                // Users can upload their own custom profile picture through the edit profile page
                String pictureUrl = (String) tokenPayload.get("picture");

                // Check if user already exists by Google ID
                Optional<User> existingUser = userRepository.findByGoogleId(googleId);

                User user;
                boolean isNewUser = false;

                if (existingUser.isPresent()) {
                    // Update existing user
                    user = existingUser.get();
                    logger.info("found existing user {}", user);
                    user.setName(name); // Update name in case it changed
                    user.setEmail(email); // Update email in case it changed
                    user.setProfilePictureUrl(pictureUrl);
                    user.updateLastLogin();
                } else {
                    // Check if user exists with same email but no Google ID (shouldn't happen with OAuth)
                    Optional<User> emailUser = userRepository.findByEmail(email);
                    if (emailUser.isPresent()) {
                        logger.warn("found user with same email {} but different google id {}. Will sync user to new google id.", email, emailUser);
                        // Link Google ID to existing email user
                        user = emailUser.get();
                        user.setGoogleId(googleId);
                        user.setName(name);
                        user.setProfilePictureUrl(pictureUrl);
                        user.updateLastLogin();
                    } else {
                        // Create completely new user

                        user = new User(googleId, email, name, pictureUrl);
                        logger.info("new user to create: {}", user);
                        isNewUser = true;
                    }
                }

                // Save user
                User savedUser = userRepository.save(user);

                logger.info("saved user: {}", savedUser);
                // TODO: Return user data or session token



                return ResponseEntity.ok().body(Map.of(
                        "message", "User authenticated successfully",
                        "id", savedUser.getId(), // Return database ID
                        "email", email,
                        "name", name,
                        "googleId", googleId,
                        "profilePictureUrl", savedUser.getProfilePictureUrl() != null ? savedUser.getProfilePictureUrl() : pictureUrl
                ));
            } else {
                logger.error("invalid ID token:{} ", idTokenString);
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID token"));
            }
        } catch (Exception e) {
            logger.error("error while processing login for {}", payload, e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    // Login/Register endpoint for Google OAuth
    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest request) {

        logger.info("user log-in attempt with payload: {}", request);
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
        logger.info("user logout");
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