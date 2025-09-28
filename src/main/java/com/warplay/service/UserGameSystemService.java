package com.warplay.service;

import com.warplay.entity.UserGameSystem;
import com.warplay.entity.User;
import com.warplay.entity.GameSystem;
import com.warplay.repository.UserGameSystemRepository;
import com.warplay.repository.UserRepository;
import com.warplay.repository.GameSystemRepository;
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
public class UserGameSystemService {

    private static final Logger logger = LoggerFactory.getLogger(UserGameSystemService.class);

    @Autowired
    private UserGameSystemRepository userGameSystemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSystemRepository gameSystemRepository;

    @Autowired
    private LoggingService loggingService;

    public List<UserGameSystem> getAllUserGameSystems() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all user-game system relationships");

            List<UserGameSystem> relationships = userGameSystemRepository.findAll();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "SELECT_ALL", true,
                    "Retrieved " + relationships.size() + " user-game system relationships");
            loggingService.logPerformance("DB_GET_ALL_USER_GAME_SYSTEMS", duration,
                    Map.of("recordCount", String.valueOf(relationships.size())));

            logger.info("Successfully retrieved {} user-game system relationships", relationships.size());
            return relationships;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "SELECT_ALL", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_ALL_USER_GAME_SYSTEMS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user-game system relationships", e);
            throw new RuntimeException("Failed to retrieve user-game system relationships", e);
        }
    }

    public List<UserGameSystem> getUserGameSystemsByUserId(Long userId) {
        logger.debug("Fetching user-game systems by user ID: {}", userId);
        return userGameSystemRepository.findByUserId(userId);
    }

    public List<UserGameSystem> getActiveUserGameSystemsByUserId(Long userId) {
        logger.debug("Fetching active user-game systems by user ID: {}", userId);
        return userGameSystemRepository.findActiveByUserId(userId);
    }

    public List<UserGameSystem> getUsersByGameSystemId(Long gameSystemId) {
        logger.debug("Fetching users by game system ID: {}", gameSystemId);
        return userGameSystemRepository.findByGameSystemId(gameSystemId);
    }

    public List<UserGameSystem> getActiveUsersByGameSystemId(Long gameSystemId) {
        logger.debug("Fetching active users by game system ID: {}", gameSystemId);
        return userGameSystemRepository.findActiveByGameSystemId(gameSystemId);
    }

    public Optional<UserGameSystem> getUserGameSystem(Long userId, Long gameSystemId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching user-game system relationship: user {} - game system {}", userId, gameSystemId);

            Optional<UserGameSystem> relationship = userGameSystemRepository.findByUserIdAndGameSystemId(userId, gameSystemId);

            long duration = System.currentTimeMillis() - startTime;

            if (relationship.isPresent()) {
                loggingService.logDatabaseOperation("user_game_systems", "SELECT_BY_USER_AND_GAME", true,
                        "Found relationship for user " + userId + " and game system " + gameSystemId);
                loggingService.logPerformance("DB_GET_USER_GAME_SYSTEM", duration,
                        Map.of("userId", userId.toString(), "gameSystemId", gameSystemId.toString(), "found", "true"));

                logger.info("Successfully retrieved user-game system relationship: user {} - game system {}", userId, gameSystemId);
            } else {
                loggingService.logDatabaseOperation("user_game_systems", "SELECT_BY_USER_AND_GAME", true,
                        "Relationship not found for user " + userId + " and game system " + gameSystemId);

                logger.warn("User-game system relationship not found: user {} - game system {}", userId, gameSystemId);
            }

            return relationship;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "SELECT_BY_USER_AND_GAME", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_USER_GAME_SYSTEM", e,
                    Map.of("userId", userId.toString(), "gameSystemId", gameSystemId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve user-game system relationship: user {} - game system {}", userId, gameSystemId, e);
            throw new RuntimeException("Failed to retrieve user-game system relationship", e);
        }
    }

    public UserGameSystem createOrUpdateUserGameSystem(UserGameSystemRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating or updating user-game system: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId());

            // Validate request
            validateUserGameSystemRequest(request);

            // Verify user and game system exist
            Optional<User> user = userRepository.findByIdActive(request.getUserId());
            Optional<GameSystem> gameSystem = gameSystemRepository.findById(request.getGameSystemId());

            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found with ID: " + request.getUserId());
            }
            if (gameSystem.isEmpty()) {
                throw new IllegalArgumentException("Game system not found with ID: " + request.getGameSystemId());
            }

            // Check if relationship already exists
            Optional<UserGameSystem> existing = userGameSystemRepository.findByUserIdAndGameSystemId(
                    request.getUserId(), request.getGameSystemId());

            UserGameSystem userGameSystem;
            boolean isUpdate = existing.isPresent();

            if (isUpdate) {
                // Update existing
                userGameSystem = existing.get();
                userGameSystem.setSkillRating(request.getSkillRating());
                userGameSystem.setYearsExperience(request.getYearsExperience());
                userGameSystem.setGamesPerYear(request.getGamesPerYear());
                userGameSystem.setNotes(request.getNotes());
                userGameSystem.setIsActive(request.getIsActive());
                userGameSystem.updateTimestamp();
            } else {
                // Create new
                userGameSystem = new UserGameSystem(user.get(), gameSystem.get());
                userGameSystem.setSkillRating(request.getSkillRating());
                userGameSystem.setYearsExperience(request.getYearsExperience());
                userGameSystem.setGamesPerYear(request.getGamesPerYear());
                userGameSystem.setNotes(request.getNotes());
                userGameSystem.setIsActive(request.getIsActive());
            }

            UserGameSystem savedUserGameSystem = userGameSystemRepository.save(userGameSystem);

            long duration = System.currentTimeMillis() - startTime;
            String operation = isUpdate ? "UPDATE" : "INSERT";
            loggingService.logDatabaseOperation("user_game_systems", operation, true,
                    operation.toLowerCase() + " user-game system relationship: user " + request.getUserId() +
                            " - game system " + request.getGameSystemId());
            loggingService.logUserAction(request.getUserId().toString(),
                    isUpdate ? "UPDATE_GAME_SYSTEM_PROFILE" : "JOIN_GAME_SYSTEM",
                    (isUpdate ? "Updated" : "Joined") + " game system: " + gameSystem.get().getName());
            loggingService.logPerformance("DB_" + operation + "_USER_GAME_SYSTEM", duration,
                    Map.of("userId", request.getUserId().toString(), "gameSystemId", request.getGameSystemId().toString()));

            logger.info("Successfully {} user-game system relationship: user {} - game system {}",
                    isUpdate ? "updated" : "created", request.getUserId(), request.getGameSystemId());

            return savedUserGameSystem;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "CREATE_OR_UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_CREATE_OR_UPDATE_USER_GAME_SYSTEM", e,
                    Map.of("userId", request.getUserId().toString(), "gameSystemId", request.getGameSystemId().toString(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create or update user-game system relationship: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId(), e);
            throw new RuntimeException("Failed to create or update user-game system relationship", e);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("VALIDATE_USER_GAME_SYSTEM", e,
                    Map.of("userId", request.getUserId().toString(), "gameSystemId", request.getGameSystemId().toString(),
                            "duration", String.valueOf(duration)));

            logger.error("User-game system validation failed: user {} - game system {}",
                    request.getUserId(), request.getGameSystemId(), e);
            throw e;
        }
    }

    public Optional<UserGameSystem> updateUserGameSystem(Long id, UserGameSystemRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating user-game system relationship: {}", id);

            Optional<UserGameSystem> existingOpt = userGameSystemRepository.findById(id);

            if (existingOpt.isPresent()) {
                // Validate request
                validateUserGameSystemRequest(request);

                UserGameSystem userGameSystem = existingOpt.get();
                userGameSystem.setSkillRating(request.getSkillRating());
                userGameSystem.setYearsExperience(request.getYearsExperience());
                userGameSystem.setGamesPerYear(request.getGamesPerYear());
                userGameSystem.setNotes(request.getNotes());
                userGameSystem.setIsActive(request.getIsActive());
                userGameSystem.updateTimestamp();

                UserGameSystem savedUserGameSystem = userGameSystemRepository.save(userGameSystem);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_game_systems", "UPDATE", true,
                        "Updated user-game system relationship ID: " + id);
                loggingService.logUserAction(userGameSystem.getUser().getId().toString(),
                        "UPDATE_GAME_SYSTEM_PROFILE",
                        "Updated game system profile: " + userGameSystem.getGameSystem().getName());
                loggingService.logPerformance("DB_UPDATE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully updated user-game system relationship: {}", id);

                return Optional.of(savedUserGameSystem);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_game_systems", "UPDATE", false,
                        "User-game system relationship not found for update");

                logger.warn("User-game system relationship not found for update: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_UPDATE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to update user-game system relationship: {}", id, e);
            throw new RuntimeException("Failed to update user-game system relationship", e);
        }
    }

    public boolean deleteUserGameSystem(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Deleting user-game system relationship: {}", id);

            if (userGameSystemRepository.existsById(id)) {
                // Get the relationship info before deleting for logging
                Optional<UserGameSystem> relationshipOpt = userGameSystemRepository.findById(id);

                userGameSystemRepository.deleteById(id);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_game_systems", "DELETE", true,
                        "Deleted user-game system relationship ID: " + id);

                if (relationshipOpt.isPresent()) {
                    UserGameSystem relationship = relationshipOpt.get();
                    loggingService.logUserAction(relationship.getUser().getId().toString(),
                            "LEAVE_GAME_SYSTEM",
                            "Left game system: " + relationship.getGameSystem().getName());
                }

                loggingService.logPerformance("DB_DELETE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully deleted user-game system relationship: {}", id);
                return true;
            } else {
                logger.warn("User-game system relationship not found for deletion: {}", id);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "DELETE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_DELETE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to delete user-game system relationship: {}", id, e);
            throw new RuntimeException("Failed to delete user-game system relationship", e);
        }
    }

    public Optional<UserGameSystem> deactivateUserGameSystem(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Deactivating user-game system relationship: {}", id);

            Optional<UserGameSystem> relationshipOpt = userGameSystemRepository.findById(id);

            if (relationshipOpt.isPresent()) {
                UserGameSystem userGameSystem = relationshipOpt.get();
                userGameSystem.setIsActive(false);
                userGameSystem.updateTimestamp();

                UserGameSystem savedUserGameSystem = userGameSystemRepository.save(userGameSystem);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_game_systems", "DEACTIVATE", true,
                        "Deactivated user-game system relationship ID: " + id);
                loggingService.logUserAction(userGameSystem.getUser().getId().toString(),
                        "DEACTIVATE_GAME_SYSTEM",
                        "Deactivated game system: " + userGameSystem.getGameSystem().getName());
                loggingService.logPerformance("DB_DEACTIVATE_USER_GAME_SYSTEM", duration,
                        Map.of("relationshipId", id.toString()));

                logger.info("Successfully deactivated user-game system relationship: {}", id);

                return Optional.of(savedUserGameSystem);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_game_systems", "DEACTIVATE", false,
                        "User-game system relationship not found for deactivation");

                logger.warn("User-game system relationship not found for deactivation: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_game_systems", "DEACTIVATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_DEACTIVATE_USER_GAME_SYSTEM", e,
                    Map.of("relationshipId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to deactivate user-game system relationship: {}", id, e);
            throw new RuntimeException("Failed to deactivate user-game system relationship", e);
        }
    }

    private void validateUserGameSystemRequest(UserGameSystemRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (request.getGameSystemId() == null) {
            throw new IllegalArgumentException("Game system ID cannot be null");
        }

        if (request.getSkillRating() != null && (request.getSkillRating() < 1 || request.getSkillRating() > 10)) {
            throw new IllegalArgumentException("Skill rating must be between 1 and 10");
        }

        if (request.getYearsExperience() != null && request.getYearsExperience() < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }

        if (request.getGamesPerYear() != null && request.getGamesPerYear() < 0) {
            throw new IllegalArgumentException("Games per year cannot be negative");
        }

        logger.debug("User-game system request validation passed for user {} - game system {}",
                request.getUserId(), request.getGameSystemId());
    }

    // Request DTO class (moved from controller)
    public static class UserGameSystemRequest {
        private Long userId;
        private Long gameSystemId;
        private Integer skillRating;
        private Integer yearsExperience;
        private Integer gamesPerYear;
        private String notes;
        private Boolean isActive = true;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getGameSystemId() { return gameSystemId; }
        public void setGameSystemId(Long gameSystemId) { this.gameSystemId = gameSystemId; }

        public Integer getSkillRating() { return skillRating; }
        public void setSkillRating(Integer skillRating) { this.skillRating = skillRating; }

        public Integer getYearsExperience() { return yearsExperience; }
        public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

        public Integer getGamesPerYear() { return gamesPerYear; }
        public void setGamesPerYear(Integer gamesPerYear) { this.gamesPerYear = gamesPerYear; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}