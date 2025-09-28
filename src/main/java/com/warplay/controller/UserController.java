package com.warplay.controller;

import com.warplay.entity.User;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    // Get all active users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAllActive();
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findByIdActive(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create or update user (for OAuth login)
    @PostMapping
    public ResponseEntity<User> createOrUpdateUser(@RequestBody User user) {
        // Check if user already exists by Google ID
        Optional<User> existingUser = userRepository.findByGoogleId(user.getGoogleId());

        if (existingUser.isPresent()) {
            // Update existing user's last login
            User existing = existingUser.get();
            existing.updateLastLogin();
            existing.setName(user.getName()); // Update name in case it changed
            existing.setProfilePictureUrl(user.getProfilePictureUrl()); // Update profile pic
            User saved = userRepository.save(existing);
            return ResponseEntity.ok(saved);
        } else {
            // Create new user
            User saved = userRepository.save(user);
            return ResponseEntity.ok(saved);
        }
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findByIdActive(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setDiscordHandle(userDetails.getDiscordHandle());
            user.setNotes(userDetails.getNotes());

            User updated = userRepository.save(user);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.notFound().build();
    }

    // Soft delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findByIdActive(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.markAsDeleted();
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Find user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}