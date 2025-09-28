package com.warplay.controller;

import com.warplay.entity.UserGameSystem;
import com.warplay.service.UserGameSystemService;
import com.warplay.service.UserGameSystemService.UserGameSystemRequest;
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
@RequestMapping("/api/user-game-systems")
public class UserGameSystemController {

    private static final Logger logger = LoggerFactory.getLogger(UserGameSystemController.class);

    @Autowired
    private UserGameSystemService userGameSystemService;

    @Autowired
    private LoggingService loggingService;

    // Get all user-game system relationships
    @GetMapping
    public ResponseEntity<List<UserGameSystem>> getAllUserGameSystems() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all user-game system relationships");

            List<UserGameSystem> relationships = userGameSystemService.getAllUserGameSystems();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("GET_ALL_USER_GAME_SYSTEMS", duration,
                    Map.of("relationshipCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} user-game system relationships", relationships.size());
            return ResponseEntity.ok(relationships);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ALL_USER_GAME_SYSTEMS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user-game system relationships", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get user-game systems by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserGameSystem>> getUserGameSystemsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user-game systems by user ID: {}", userId);

            List<UserGameSystem> relationships = userGameSystemService.getUserGameSystemsByUserId(userId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_USER_GAME_SYSTEMS",
                    "Viewed game systems for user: " + userId);
            loggingService.logPerformance("GET_USER_GAME_SYSTEMS_BY_USER", duration,
                    Map.of("userId", userId.toString(), "relationshipCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} game systems for user: {}", relationships.size(), userId);
            return ResponseEntity.ok(relationships);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER_GAME_SYSTEMS_BY_USER", e,
                    Map.of("userId", userId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user-game systems by user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get active user-game systems by user ID
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<UserGameSystem>> getActiveUserGameSystemsByUserId(@PathVariable Long userId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active user-game systems by user ID: {}", userId);

            List<UserGameSystem> relationships = userGameSystemService.getActiveUserGameSystemsByUserId(userId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_ACTIVE_USER_GAME_SYSTEMS",
                    "Viewed active game systems for user: " + userId);
            loggingService.logPerformance("GET_ACTIVE_USER_GAME_SYSTEMS_BY_USER", duration,
                    Map.of("userId", userId.toString(), "activeRelationshipCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} active game systems for user: {}", relationships.size(), userId);
            return ResponseEntity.ok(relationships);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ACTIVE_USER_GAME_SYSTEMS_BY_USER", e,
                    Map.of("userId", userId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve active user-game systems by user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get users by game system ID
    @GetMapping("/game-system/{gameSystemId}")
    public ResponseEntity<List<UserGameSystem>> getUsersByGameSystemId(@PathVariable Long gameSystemId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching users by game system ID: {}", gameSystemId);

            List<UserGameSystem> relationships = userGameSystemService.getUsersByGameSystemId(gameSystemId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_USERS_BY_GAME_SYSTEM",
                    "Viewed users for game system: " + gameSystemId);
            loggingService.logPerformance("GET_USERS_BY_GAME_SYSTEM", duration,
                    Map.of("gameSystemId", gameSystemId.toString(), "userCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} users for game system: {}", relationships.size(), gameSystemId);
            return ResponseEntity.ok(relationships);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USERS_BY_GAME_SYSTEM", e,
                    Map.of("gameSystemId", gameSystemId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve users by game system ID: {}", gameSystemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get active users by game system ID
    @GetMapping("/game-system/{gameSystemId}/active")
    public ResponseEntity<List<UserGameSystem>> getActiveUsersByGameSystemId(@PathVariable Long gameSystemId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active users by game system ID: {}", gameSystemId);

            List<UserGameSystem> relationships = userGameSystemService.getActiveUsersByGameSystemId(gameSystemId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_ACTIVE_USERS_BY_GAME_SYSTEM",
                    "Viewed active users for game system: " + gameSystemId);
            loggingService.logPerformance("GET_ACTIVE_USERS_BY_GAME_SYSTEM", duration,
                    Map.of("gameSystemId", gameSystemId.toString(), "activeUserCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} active users for game system: {}", relationships.size(), gameSystemId);
            return ResponseEntity.ok(relationships);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ACTIVE_USERS_BY_GAME_SYSTEM", e,
                    Map.of("gameSystemId", gameSystemId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve active users by game system ID: {}", gameSystemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get specific user-game system relationship
    @GetMapping("/user/{userId}/game-system/{gameSystemId}")
    public ResponseEntity<UserGameSystem> getUserGameSystem(@PathVariable Long userId, @PathVariable Long gameSystemId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user-game system relationship: user {} - game system {}", userId, gameSystemId);

            Optional<UserGameSystem> relationship = userGameSystemService.getUserGameSystem(userId, gameSystemId);

            if (relationship.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_USER_GAME_SYSTEM_RELATIONSHIP",
                        "Viewed relationship: user " + userId + " - game system " + gameSystemId);
                loggingService.logPerformance("GET_USER_GAME_SYSTEM_RELATIONSHIP", duration,
                        Map.of("userId", userId.toString(), "gameSystemId", gameSystemId.toString()));

                logger.info("Successfully retrieved user-game system relationship: user {} - game system {}", userId, gameSystemId);
                return ResponseEntity.ok(relationship.get());
            } else {
                logger.warn("User-game system relationship not found: user {} - game system {}", userId, gameSystemId);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER_GAME_SYSTEM_RELATIONSHIP", e,
                    Map.of("userId", userId.toString(), "gameSystemId", gameSystemId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user-game system relationship: user {} - game system {}", userId, gameSystemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create or update user-game system relationship
    @PostMapping
    public ResponseEntity<UserGameSystem> createOrUpdateUserGameSystem(@Valid @RequestBody UserGameSystemRequest request) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Creating or updating user-game system: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId());

            UserGameSystem savedRelationship = userGameSystemService.createOrUpdateUserGameSystem(request);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("CREATE_OR_UPDATE_USER_GAME_SYSTEM", duration,
                    Map.of("userId", request.getUserId().toString(),
                            "gameSystemId", request.getGameSystemId().toString(),
                            "relationshipId", savedRelationship.getId().toString()));

            logger.info("Successfully created or updated user-game system relationship: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId());

            return ResponseEntity.ok(savedRelationship);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_OR_UPDATE_USER_GAME_SYSTEM_VALIDATION", e,
                    Map.of("userId", request.getUserId().toString(),
                            "gameSystemId", request.getGameSystemId().toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.warn("User-game system creation/update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_OR_UPDATE_USER_GAME_SYSTEM", e,
                    Map.of("userId", request.getUserId().toString(),
                            "gameSystemId", request.getGameSystemId().toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create or update user-game system relationship: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update user-game system relationship
    @PutMapping("/{id}")
    public ResponseEntity<UserGameSystem> updateUserGameSystem(@PathVariable Long id, @Valid @RequestBody UserGameSystemRequest request) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Updating user-game system relationship: {}", id);

            Optional<UserGameSystem> updatedRelationship = userGameSystemService.updateUserGameSystem(id, request);

            if (updatedRelationship.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logPerformance("UPDATE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully updated user-game system relationship: {}", id);
                return ResponseEntity.ok(updatedRelationship.get());
            } else {
                logger.warn("User-game system relationship not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_USER_GAME_SYSTEM_VALIDATION", e,
                    Map.of("relationshipId", id.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.warn("User-game system update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update user-game system relationship: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete user-game system relationship
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserGameSystem(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Deleting user-game system relationship: {}", id);

            boolean deleted = userGameSystemService.deleteUserGameSystem(id);

            if (deleted) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(currentUserId, "DELETE_USER_GAME_SYSTEM",
                        "Deleted user-game system relationship ID: " + id);
                loggingService.logPerformance("DELETE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully deleted user-game system relationship: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("User-game system relationship not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DELETE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to delete user-game system relationship: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Set user-game system as inactive
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserGameSystem> deactivateUserGameSystem(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Deactivating user-game system relationship: {}", id);

            Optional<UserGameSystem> deactivatedRelationship = userGameSystemService.deactivateUserGameSystem(id);

            if (deactivatedRelationship.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(currentUserId, "DEACTIVATE_USER_GAME_SYSTEM",
                        "Deactivated user-game system relationship ID: " + id);
                loggingService.logPerformance("DEACTIVATE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully deactivated user-game system relationship: {}", id);
                return ResponseEntity.ok(deactivatedRelationship.get());
            } else {
                logger.warn("User-game system relationship not found for deactivation: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DEACTIVATE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to deactivate user-game system relationship: {}", id, e);
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