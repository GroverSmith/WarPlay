package com.warplay.controller;

import com.warplay.entity.GameSystem;
import com.warplay.service.GameSystemService;
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
@RequestMapping("/api/game-systems")
public class GameSystemController {

    private static final Logger logger = LoggerFactory.getLogger(GameSystemController.class);

    @Autowired
    private GameSystemService gameSystemService;

    @Autowired
    private LoggingService loggingService;

    // Get all game systems
    @GetMapping
    public ResponseEntity<List<GameSystem>> getAllGameSystems() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all game systems");

            List<GameSystem> gameSystems = gameSystemService.getAllGameSystems();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("GET_ALL_GAME_SYSTEMS", duration,
                    Map.of("gameSystemCount", String.valueOf(gameSystems.size())));

            logger.info("Successfully retrieved {} game systems", gameSystems.size());
            return ResponseEntity.ok(gameSystems);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ALL_GAME_SYSTEMS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game systems", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get game system by ID
    @GetMapping("/{id}")
    public ResponseEntity<GameSystem> getGameSystemById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching game system with ID: {}", id);

            Optional<GameSystem> gameSystem = gameSystemService.getGameSystemById(id);

            if (gameSystem.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_GAME_SYSTEM",
                        "Viewed game system: " + gameSystem.get().getName());
                loggingService.logPerformance("GET_GAME_SYSTEM", duration,
                        Map.of("gameSystemId", id.toString()));

                logger.info("Successfully retrieved game system: {}", id);
                return ResponseEntity.ok(gameSystem.get());
            } else {
                logger.warn("Game system not found: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_GAME_SYSTEM", e,
                    Map.of("gameSystemId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game system: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new game system
    @PostMapping
    public ResponseEntity<GameSystem> createGameSystem(@Valid @RequestBody GameSystem gameSystem) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Creating new game system: {}", gameSystem.getName());

            GameSystem savedGameSystem = gameSystemService.createGameSystem(gameSystem);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(userId, "CREATE_GAME_SYSTEM",
                    "Created game system: " + savedGameSystem.getName() + " by " + savedGameSystem.getPublisher());
            loggingService.logPerformance("CREATE_GAME_SYSTEM", duration,
                    Map.of("gameSystemId", savedGameSystem.getId().toString(),
                            "publisher", savedGameSystem.getPublisher() != null ? savedGameSystem.getPublisher() : "unknown"));

            logger.info("Successfully created game system: {} (ID: {})",
                    savedGameSystem.getName(), savedGameSystem.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedGameSystem);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_GAME_SYSTEM_VALIDATION", e,
                    Map.of("userId", userId, "gameSystemName", gameSystem.getName(),
                            "duration", String.valueOf(duration)));

            logger.warn("Game system creation failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_GAME_SYSTEM", e,
                    Map.of("userId", userId, "gameSystemName", gameSystem.getName(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create game system: {}", gameSystem.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update game system
    @PutMapping("/{id}")
    public ResponseEntity<GameSystem> updateGameSystem(@PathVariable Long id, @Valid @RequestBody GameSystem gameSystemDetails) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Updating game system: {}", id);

            Optional<GameSystem> updatedGameSystem = gameSystemService.updateGameSystem(id, gameSystemDetails);

            if (updatedGameSystem.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userId, "UPDATE_GAME_SYSTEM",
                        "Updated game system: " + updatedGameSystem.get().getName());
                loggingService.logPerformance("UPDATE_GAME_SYSTEM", duration,
                        Map.of("gameSystemId", id.toString()));

                logger.info("Successfully updated game system: {}", id);
                return ResponseEntity.ok(updatedGameSystem.get());
            } else {
                logger.warn("Game system not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_GAME_SYSTEM_VALIDATION", e,
                    Map.of("gameSystemId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.warn("Game system update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_GAME_SYSTEM", e,
                    Map.of("gameSystemId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update game system: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Search game systems by name
    @GetMapping("/search")
    public ResponseEntity<List<GameSystem>> searchGameSystems(@RequestParam String query) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Searching game systems by query: {}", query);

            List<GameSystem> gameSystems = gameSystemService.searchGameSystems(query);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "SEARCH_GAME_SYSTEMS",
                    "Searched game systems by query: " + query);
            loggingService.logPerformance("SEARCH_GAME_SYSTEMS", duration,
                    Map.of("query", query, "resultCount", String.valueOf(gameSystems.size())));

            logger.info("Successfully found {} game systems matching query: {}", gameSystems.size(), query);
            return ResponseEntity.ok(gameSystems);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("SEARCH_GAME_SYSTEMS", e,
                    Map.of("query", query, "duration", String.valueOf(duration)));

            logger.error("Failed to search game systems by query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get game systems by publisher
    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<List<GameSystem>> getGameSystemsByPublisher(@PathVariable String publisher) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching game systems by publisher: {}", publisher);

            List<GameSystem> gameSystems = gameSystemService.getGameSystemsByPublisher(publisher);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_GAME_SYSTEMS_BY_PUBLISHER",
                    "Viewed game systems by publisher: " + publisher);
            loggingService.logPerformance("GET_GAME_SYSTEMS_BY_PUBLISHER", duration,
                    Map.of("publisher", publisher, "resultCount", String.valueOf(gameSystems.size())));

            logger.info("Successfully retrieved {} game systems for publisher: {}",
                    gameSystems.size(), publisher);
            return ResponseEntity.ok(gameSystems);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_GAME_SYSTEMS_BY_PUBLISHER", e,
                    Map.of("publisher", publisher, "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game systems by publisher: {}", publisher, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get statistics for a game system
    @GetMapping("/{id}/stats")
    public ResponseEntity<GameSystemService.GameSystemStats> getGameSystemStats(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching game system statistics: {}", id);

            GameSystemService.GameSystemStats stats = gameSystemService.getGameSystemStats(id);

            if (stats != null) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_GAME_SYSTEM_STATS",
                        "Viewed stats for game system: " + stats.getGameSystemName());
                loggingService.logPerformance("GET_GAME_SYSTEM_STATS", duration,
                        Map.of("gameSystemId", id.toString(),
                                "playerCount", stats.getActivePlayerCount().toString()));

                logger.info("Successfully retrieved stats for game system: {} - {} players",
                        stats.getGameSystemName(), stats.getActivePlayerCount());
                return ResponseEntity.ok(stats);
            } else {
                logger.warn("Game system not found for stats: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_GAME_SYSTEM_STATS", e,
                    Map.of("gameSystemId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve game system stats: {}", id, e);
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