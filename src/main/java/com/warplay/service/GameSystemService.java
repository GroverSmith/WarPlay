package com.warplay.service;

import com.warplay.entity.GameSystem;
import com.warplay.repository.GameSystemRepository;
import com.warplay.repository.UserGameSystemRepository;
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
public class GameSystemService {

    private static final Logger logger = LoggerFactory.getLogger(GameSystemService.class);

    @Autowired
    private GameSystemRepository gameSystemRepository;

    @Autowired
    private UserGameSystemRepository userGameSystemRepository;

    @Autowired
    private LoggingService loggingService;

    public List<GameSystem> getAllGameSystems() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all game systems from database");

            List<GameSystem> gameSystems = gameSystemRepository.findAllOrderedByName();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "SELECT_ALL", true,
                    "Retrieved " + gameSystems.size() + " game systems");
            loggingService.logPerformance("DB_GET_ALL_GAME_SYSTEMS", duration,
                    Map.of("recordCount", String.valueOf(gameSystems.size())));

            logger.info("Successfully retrieved {} game systems from database", gameSystems.size());
            return gameSystems;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "SELECT_ALL", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_ALL_GAME_SYSTEMS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game systems from database", e);
            throw new RuntimeException("Failed to retrieve game systems", e);
        }
    }

    public Optional<GameSystem> getGameSystemById(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching game system by ID: {}", id);

            Optional<GameSystem> gameSystem = gameSystemRepository.findById(id);

            long duration = System.currentTimeMillis() - startTime;

            if (gameSystem.isPresent()) {
                loggingService.logDatabaseOperation("game_systems", "SELECT_BY_ID", true,
                        "Found game system: " + gameSystem.get().getName());
                loggingService.logPerformance("DB_GET_GAME_SYSTEM_BY_ID", duration,
                        Map.of("gameSystemId", id.toString(), "found", "true"));

                logger.info("Successfully retrieved game system: {} (ID: {})",
                        gameSystem.get().getName(), id);
            } else {
                loggingService.logDatabaseOperation("game_systems", "SELECT_BY_ID", true,
                        "Game system not found");
                loggingService.logPerformance("DB_GET_GAME_SYSTEM_BY_ID", duration,
                        Map.of("gameSystemId", id.toString(), "found", "false"));

                logger.warn("Game system not found: {}", id);
            }

            return gameSystem;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "SELECT_BY_ID", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_GET_GAME_SYSTEM_BY_ID", e,
                    Map.of("gameSystemId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game system by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve game system", e);
        }
    }

    public GameSystem createGameSystem(GameSystem gameSystem) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating new game system: {}", gameSystem.getName());

            // Validate game system
            validateGameSystem(gameSystem);

            // Check if name already exists
            if (gameSystemRepository.existsByName(gameSystem.getName())) {
                throw new IllegalArgumentException(
                        "A game system with this name already exists: " + gameSystem.getName());
            }

            GameSystem savedGameSystem = gameSystemRepository.save(gameSystem);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "INSERT", true,
                    "Created game system: " + savedGameSystem.getName() + " (ID: " + savedGameSystem.getId() + ")");
            loggingService.logPerformance("DB_CREATE_GAME_SYSTEM", duration,
                    Map.of("gameSystemId", savedGameSystem.getId().toString(),
                            "gameSystemName", savedGameSystem.getName()));

            logger.info("Successfully created game system: {} (ID: {})",
                    savedGameSystem.getName(), savedGameSystem.getId());

            return savedGameSystem;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "INSERT", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_CREATE_GAME_SYSTEM", e,
                    Map.of("gameSystemName", gameSystem.getName(), "duration", String.valueOf(duration)));

            logger.error("Failed to create game system: {}", gameSystem.getName(), e);
            throw new RuntimeException("Failed to create game system", e);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("VALIDATE_GAME_SYSTEM", e,
                    Map.of("gameSystemName", gameSystem.getName(), "duration", String.valueOf(duration)));

            logger.error("Game system validation failed: {}", gameSystem.getName(), e);
            throw e;
        }
    }

    public Optional<GameSystem> updateGameSystem(Long id, GameSystem gameSystemDetails) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating game system: {}", id);

            Optional<GameSystem> existingGameSystemOpt = gameSystemRepository.findById(id);

            if (existingGameSystemOpt.isPresent()) {
                GameSystem existingGameSystem = existingGameSystemOpt.get();
                String oldName = existingGameSystem.getName();

                // Validate updated game system
                validateGameSystem(gameSystemDetails);

                // Check if new name conflicts with existing (excluding current)
                if (!existingGameSystem.getName().equals(gameSystemDetails.getName()) &&
                        gameSystemRepository.existsByName(gameSystemDetails.getName())) {
                    throw new IllegalArgumentException(
                            "A game system with this name already exists: " + gameSystemDetails.getName());
                }

                // Update fields
                existingGameSystem.setName(gameSystemDetails.getName());
                existingGameSystem.setShortName(gameSystemDetails.getShortName());
                existingGameSystem.setDescription(gameSystemDetails.getDescription());
                existingGameSystem.setPublisher(gameSystemDetails.getPublisher());
                existingGameSystem.setIconUrl(gameSystemDetails.getIconUrl());

                GameSystem savedGameSystem = gameSystemRepository.save(existingGameSystem);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("game_systems", "UPDATE", true,
                        "Updated game system from '" + oldName + "' to '" + savedGameSystem.getName() + "'");
                loggingService.logPerformance("DB_UPDATE_GAME_SYSTEM", duration,
                        Map.of("gameSystemId", id.toString(), "gameSystemName", savedGameSystem.getName()));

                logger.info("Successfully updated game system: {} (ID: {})",
                        savedGameSystem.getName(), id);

                return Optional.of(savedGameSystem);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("game_systems", "UPDATE", false,
                        "Game system not found for update");

                logger.warn("Game system not found for update: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("game_systems", "UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_UPDATE_GAME_SYSTEM", e,
                    Map.of("gameSystemId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to update game system: {}", id, e);
            throw new RuntimeException("Failed to update game system", e);
        }
    }



    // Search and filter methods
    public List<GameSystem> searchGameSystems(String query) {
        logger.debug("Searching game systems by query: {}", query);
        return gameSystemRepository.searchByNameContaining(query);
    }

    public List<GameSystem> getGameSystemsByPublisher(String publisher) {
        logger.debug("Fetching game systems by publisher: {}", publisher);
        return gameSystemRepository.findByPublisher(publisher);
    }

    // Statistics method
    public GameSystemStats getGameSystemStats(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching game system statistics: {}", id);

            Optional<GameSystem> gameSystem = gameSystemRepository.findById(id);

            if (gameSystem.isPresent()) {
                Long playerCount = userGameSystemRepository.countActivePlayersByGameSystemId(id);
                Double avgSkillRating = userGameSystemRepository.getAverageSkillRatingByGameSystemId(id);

                GameSystemStats stats = new GameSystemStats(
                        gameSystem.get().getName(),
                        playerCount,
                        avgSkillRating
                );

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logPerformance("DB_GET_GAME_SYSTEM_STATS", duration,
                        Map.of("gameSystemId", id.toString(), "playerCount", playerCount.toString()));

                logger.info("Successfully retrieved stats for game system: {} - {} players",
                        gameSystem.get().getName(), playerCount);

                return stats;
            } else {
                logger.warn("Game system not found for stats: {}", id);
                return null;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DB_GET_GAME_SYSTEM_STATS", e,
                    Map.of("gameSystemId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game system stats: {}", id, e);
            throw new RuntimeException("Failed to retrieve game system stats", e);
        }
    }

    private void validateGameSystem(GameSystem gameSystem) {
        if (gameSystem.getName() == null || gameSystem.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Game system name cannot be empty");
        }

        if (gameSystem.getName().length() > 100) {
            throw new IllegalArgumentException("Game system name cannot exceed 100 characters");
        }

        if (gameSystem.getShortName() != null && gameSystem.getShortName().length() > 20) {
            throw new IllegalArgumentException("Short name cannot exceed 20 characters");
        }

        if (gameSystem.getPublisher() != null && gameSystem.getPublisher().length() > 100) {
            throw new IllegalArgumentException("Publisher cannot exceed 100 characters");
        }

        logger.debug("Game system validation passed for: {}", gameSystem.getName());
    }

    // Inner class for statistics
    public static class GameSystemStats {
        private final String gameSystemName;
        private final Long activePlayerCount;
        private final Double averageSkillRating;

        public GameSystemStats(String gameSystemName, Long activePlayerCount, Double averageSkillRating) {
            this.gameSystemName = gameSystemName;
            this.activePlayerCount = activePlayerCount;
            this.averageSkillRating = averageSkillRating;
        }

        public String getGameSystemName() { return gameSystemName; }
        public Long getActivePlayerCount() { return activePlayerCount; }
        public Double getAverageSkillRating() { return averageSkillRating; }
    }
}