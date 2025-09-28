package com.warplay.service;

import com.warplay.entity.User;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggingService loggingService;

    public List<User> getAllActiveUsers() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active users from database");

            List<User> users = userRepository.findAllActive();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "SELECT_ALL_ACTIVE", true,
                    "Retrieved " + users.size() + " active users");
            loggingService.logPerformance("DB_GET_ALL_ACTIVE_USERS", duration,
                    Map.of("recordCount", String.valueOf(users.size())));

            logger.info("Successfully retrieved {} active users from database", users.size());
            return users;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "SELECT_ALL_ACTIVE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_ALL_ACTIVE_USERS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve active users from database", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    public Optional<User> getActiveUserById(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active user by ID: {}", id);

            Optional<User> user = userRepository.findByIdActive(id);

            long duration = System.currentTimeMillis() - startTime;

            if (user.isPresent()) {
                loggingService.logDatabaseOperation("users", "SELECT_BY_ID", true,
                        "Found user: " + user.get().getName());
                loggingService.logPerformance("DB_GET_USER_BY_ID", duration,
                        Map.of("userId", id.toString(), "found", "true"));

                logger.info("Successfully retrieved user: {} (ID: {})",
                        user.get().getName(), id);
            } else {
                loggingService.logDatabaseOperation("users", "SELECT_BY_ID", true,
                        "User not found or deleted");
                loggingService.logPerformance("DB_GET_USER_BY_ID", duration,
                        Map.of("userId", id.toString(), "found", "false"));

                logger.warn("Active user not found: {}", id);
            }

            return user;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "SELECT_BY_ID", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_USER_BY_ID", e,
                    Map.of("userId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    public User createOrUpdateUser(User user) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating or updating user with Google ID: {}", user.getGoogleId());

            // Validate user
            validateUser(user);

            // Check if user already exists by Google ID
            Optional<User> existingUser = userRepository.findByGoogleId(user.getGoogleId());

            User savedUser;
            if (existingUser.isPresent()) {
                // Update existing user's last login and info
                User existing = existingUser.get();
                existing.updateLastLogin();
                existing.setName(user.getName()); // Update name in case it changed
                existing.setProfilePictureUrl(user.getProfilePictureUrl()); // Update profile pic

                savedUser = userRepository.save(existing);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "UPDATE_LOGIN", true,
                        "Updated user login: " + savedUser.getName());
                loggingService.logUserAction(savedUser.getId().toString(), "LOGIN",
                        "User logged in via OAuth");
                loggingService.logPerformance("DB_UPDATE_USER_LOGIN", duration,
                        Map.of("userId", savedUser.getId().toString()));

                logger.info("Successfully updated user login: {} (ID: {})",
                        savedUser.getName(), savedUser.getId());
            } else {
                // Create new user
                savedUser = userRepository.save(user);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "INSERT", true,
                        "Created new user: " + savedUser.getName() + " (ID: " + savedUser.getId() + ")");
                loggingService.logUserAction(savedUser.getId().toString(), "REGISTER",
                        "New user registered via OAuth");
                loggingService.logPerformance("DB_CREATE_USER", duration,
                        Map.of("userId", savedUser.getId().toString()));

                logger.info("Successfully created new user: {} (ID: {})",
                        savedUser.getName(), savedUser.getId());
            }

            return savedUser;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "CREATE_OR_UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_CREATE_OR_UPDATE_USER", e,
                    Map.of("googleId", user.getGoogleId(), "duration", String.valueOf(duration)));

            logger.error("Failed to create or update user with Google ID: {}", user.getGoogleId(), e);
            throw new RuntimeException("Failed to create or update user", e);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("VALIDATE_USER", e,
                    Map.of("googleId", user.getGoogleId(), "duration", String.valueOf(duration)));

            logger.error("User validation failed for Google ID: {}", user.getGoogleId(), e);
            throw e;
        }
    }

    public Optional<User> updateUser(Long id, User userDetails) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating user: {}", id);

            Optional<User> existingUserOpt = userRepository.findByIdActive(id);

            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();
                String oldName = existingUser.getName();

                // Validate updated user details
                validateUserUpdate(userDetails);

                // Update fields
                existingUser.setName(userDetails.getName());
                existingUser.setDiscordHandle(userDetails.getDiscordHandle());
                existingUser.setNotes(userDetails.getNotes());

                User savedUser = userRepository.save(existingUser);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "UPDATE", true,
                        "Updated user profile: " + savedUser.getName());
                loggingService.logUserAction(id.toString(), "UPDATE_PROFILE",
                        "Updated profile information");
                loggingService.logPerformance("DB_UPDATE_USER", duration,
                        Map.of("userId", id.toString()));

                logger.info("Successfully updated user: {} (ID: {})",
                        savedUser.getName(), id);

                return Optional.of(savedUser);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "UPDATE", false,
                        "User not found for update");

                logger.warn("Active user not found for update: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_UPDATE_USER", e,
                    Map.of("userId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to update user: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public boolean softDeleteUser(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Soft deleting user: {}", id);

            Optional<User> userOpt = userRepository.findByIdActive(id);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.markAsDeleted();
                userRepository.save(user);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "SOFT_DELETE", true,
                        "Soft deleted user: " + user.getName() + " (ID: " + id + ")");
                loggingService.logUserAction(id.toString(), "ACCOUNT_DELETED",
                        "User account was soft deleted");
                loggingService.logPerformance("DB_SOFT_DELETE_USER", duration,
                        Map.of("userId", id.toString()));

                logger.info("Successfully soft deleted user: {} (ID: {})",
                        user.getName(), id);
                return true;
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("users", "SOFT_DELETE", false,
                        "User not found for soft deletion");

                logger.warn("Active user not found for soft deletion: {}", id);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "SOFT_DELETE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_SOFT_DELETE_USER", e,
                    Map.of("userId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to soft delete user: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user by email: {}", email);

            Optional<User> user = userRepository.findByEmail(email);

            long duration = System.currentTimeMillis() - startTime;

            if (user.isPresent()) {
                loggingService.logDatabaseOperation("users", "SELECT_BY_EMAIL", true,
                        "Found user by email");
                loggingService.logPerformance("DB_GET_USER_BY_EMAIL", duration,
                        Map.of("email", email, "found", "true"));

                logger.info("Successfully retrieved user by email: {}", email);
            } else {
                loggingService.logDatabaseOperation("users", "SELECT_BY_EMAIL", true,
                        "User not found by email");
                loggingService.logPerformance("DB_GET_USER_BY_EMAIL", duration,
                        Map.of("email", email, "found", "false"));

                logger.warn("User not found by email: {}", email);
            }

            return user;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("users", "SELECT_BY_EMAIL", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_USER_BY_EMAIL", e,
                    Map.of("email", email, "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user by email: {}", email, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    private void validateUser(User user) {
        if (user.getGoogleId() == null || user.getGoogleId().trim().isEmpty()) {
            throw new IllegalArgumentException("Google ID cannot be empty");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }

        if (user.getName().length() > 100) {
            throw new IllegalArgumentException("User name cannot exceed 100 characters");
        }

        logger.debug("User validation passed for: {}", user.getName());
    }

    private void validateUserUpdate(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }

        if (user.getName().length() > 100) {
            throw new IllegalArgumentException("User name cannot exceed 100 characters");
        }

        // Discord handle validation
        if (user.getDiscordHandle() != null && user.getDiscordHandle().length() > 50) {
            throw new IllegalArgumentException("Discord handle cannot exceed 50 characters");
        }

        logger.debug("User update validation passed for: {}", user.getName());
    }
}