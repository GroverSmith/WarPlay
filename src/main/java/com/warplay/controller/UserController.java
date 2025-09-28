package com.warplay.controller;

import com.warplay.entity.User;
import com.warplay.service.UserService;
import com.warplay.service.LoggingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingService loggingService;

    // Get all active users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active users");

            List<User> users = userService.getAllActiveUsers();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("GET_ALL_USERS", duration,
                    Map.of("userCount", String.valueOf(users.size())));

            logger.info("Successfully retrieved {} active users", users.size());
            return ResponseEntity.ok(users);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ALL_USERS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user with ID: {}", id);

            Optional<User> user = userService.getActiveUserById(id);

            if (user.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_USER",
                        "Viewed user: " + user.get().getName());
                loggingService.logPerformance("GET_USER", duration,
                        Map.of("userId", id.toString()));

                logger.info("Successfully retrieved user: {}", id);
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("Active user not found: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER", e,
                    Map.of("userId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create or update user (for OAuth login)
    @PostMapping
    public ResponseEntity<User> createOrUpdateUser(@Valid @RequestBody User user) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating or updating user with Google ID: {}", user.getGoogleId());

            User savedUser = userService.createOrUpdateUser(user);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("CREATE_OR_UPDATE_USER", duration,
                    Map.of("userId", savedUser.getId().toString(),
                            "googleId", savedUser.getGoogleId()));

            logger.info("Successfully created or updated user: {} (ID: {})",
                    savedUser.getName(), savedUser.getId());

            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_OR_UPDATE_USER_VALIDATION", e,
                    Map.of("googleId", user.getGoogleId(),
                            "duration", String.valueOf(duration)));

            logger.warn("User creation/update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_OR_UPDATE_USER", e,
                    Map.of("googleId", user.getGoogleId(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create or update user with Google ID: {}", user.getGoogleId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update user profile
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Updating user: {}", id);

            Optional<User> updatedUser = userService.updateUser(id, userDetails);

            if (updatedUser.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(currentUserId, "UPDATE_USER",
                        "Updated user: " + updatedUser.get().getName());
                loggingService.logPerformance("UPDATE_USER", duration,
                        Map.of("userId", id.toString()));

                logger.info("Successfully updated user: {}", id);
                return ResponseEntity.ok(updatedUser.get());
            } else {
                logger.warn("Active user not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_USER_VALIDATION", e,
                    Map.of("userId", id.toString(), "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.warn("User update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_USER", e,
                    Map.of("userId", id.toString(), "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Soft delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Soft deleting user: {}", id);

            boolean deleted = userService.softDeleteUser(id);

            if (deleted) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(currentUserId, "DELETE_USER",
                        "Deleted user ID: " + id);
                loggingService.logPerformance("DELETE_USER", duration,
                        Map.of("userId", id.toString()));

                logger.info("Successfully soft deleted user: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Active user not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DELETE_USER", e,
                    Map.of("userId", id.toString(), "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to delete user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Find user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user by email: {}", email);

            Optional<User> user = userService.getUserByEmail(email);

            if (user.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_USER_BY_EMAIL",
                        "Viewed user by email: " + email);
                loggingService.logPerformance("GET_USER_BY_EMAIL", duration,
                        Map.of("email", email));

                logger.info("Successfully retrieved user by email: {}", email);
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("User not found by email: {}", email);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER_BY_EMAIL", e,
                    Map.of("email", email, "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getCurrentUserId() {
        // Implement based on your authentication mechanism
        // This is a placeholder - adjust based on your security setup

        // If using Spring Security:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
        //     return auth.getName();
        // }

        // For now, return a default value
        return "system";
    }
}