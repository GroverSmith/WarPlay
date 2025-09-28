package com.warplay.controller;

import com.warplay.entity.Club;
import com.warplay.service.ClubService;
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
@RequestMapping("/api/clubs")
public class ClubController {

    private static final Logger logger = LoggerFactory.getLogger(ClubController.class);

    @Autowired
    private ClubService clubService;

    @Autowired
    private LoggingService loggingService;

    @GetMapping
    public ResponseEntity<List<Club>> getAllActiveClubs() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active clubs");

            List<Club> clubs = clubService.getAllActiveClubs();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("GET_ALL_CLUBS", duration,
                    Map.of("clubCount", String.valueOf(clubs.size())));

            logger.info("Successfully retrieved {} active clubs", clubs.size());
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ALL_CLUBS", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve clubs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getClub(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching club with ID: {}", id);

            Optional<Club> club = clubService.getActiveClubById(id);

            if (club.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUB",
                        "Viewed club: " + club.get().getName());
                loggingService.logPerformance("GET_CLUB", duration,
                        Map.of("clubId", id.toString()));

                logger.info("Successfully retrieved club: {}", id);
                return ResponseEntity.ok(club.get());
            } else {
                logger.warn("Active club not found: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_CLUB", e,
                    Map.of("clubId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve club: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Club> createClub(@Valid @RequestBody Club club) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Creating new club: {}", club.getName());

            Club savedClub = clubService.createClub(club);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(userId, "CREATE_CLUB",
                    "Created club: " + savedClub.getName() + " for " + savedClub.getGameSystem());
            loggingService.logPerformance("CREATE_CLUB", duration,
                    Map.of("clubId", savedClub.getId().toString(),
                            "gameSystem", savedClub.getGameSystem()));

            logger.info("Successfully created club: {} (ID: {})",
                    savedClub.getName(), savedClub.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedClub);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CLUB_VALIDATION", e,
                    Map.of("userId", userId, "clubName", club.getName(),
                            "duration", String.valueOf(duration)));

            logger.warn("Club creation failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CLUB", e,
                    Map.of("userId", userId, "clubName", club.getName(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create club: {}", club.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @Valid @RequestBody Club club) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Updating club: {}", id);

            Optional<Club> updatedClub = clubService.updateClub(id, club);

            if (updatedClub.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userId, "UPDATE_CLUB",
                        "Updated club: " + updatedClub.get().getName());
                loggingService.logPerformance("UPDATE_CLUB", duration,
                        Map.of("clubId", id.toString()));

                logger.info("Successfully updated club: {}", id);
                return ResponseEntity.ok(updatedClub.get());
            } else {
                logger.warn("Active club not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CLUB_VALIDATION", e,
                    Map.of("clubId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.warn("Club update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CLUB", e,
                    Map.of("clubId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update club: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Soft deleting club: {}", id);

            boolean deleted = clubService.softDeleteClub(id);

            if (deleted) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userId, "DELETE_CLUB",
                        "Deleted club ID: " + id);
                loggingService.logPerformance("DELETE_CLUB", duration,
                        Map.of("clubId", id.toString()));

                logger.info("Successfully soft deleted club: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Active club not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DELETE_CLUB", e,
                    Map.of("clubId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to delete club: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search and filter endpoints
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Club>> getClubsByOwner(@PathVariable Long ownerId) {
        try {
            logger.debug("Fetching clubs by owner: {}", ownerId);

            List<Club> clubs = clubService.getClubsByOwner(ownerId);

            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUBS_BY_OWNER",
                    "Viewed clubs for owner: " + ownerId);

            logger.info("Successfully retrieved {} clubs for owner: {}", clubs.size(), ownerId);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            loggingService.logError("GET_CLUBS_BY_OWNER", e,
                    Map.of("ownerId", ownerId.toString()));

            logger.error("Failed to retrieve clubs by owner: {}", ownerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/game-system/{gameSystem}")
    public ResponseEntity<List<Club>> getClubsByGameSystem(@PathVariable String gameSystem) {
        try {
            logger.debug("Fetching clubs by game system: {}", gameSystem);

            List<Club> clubs = clubService.getClubsByGameSystem(gameSystem);

            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUBS_BY_GAME_SYSTEM",
                    "Viewed clubs for game system: " + gameSystem);

            logger.info("Successfully retrieved {} clubs for game system: {}",
                    clubs.size(), gameSystem);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            loggingService.logError("GET_CLUBS_BY_GAME_SYSTEM", e,
                    Map.of("gameSystem", gameSystem));

            logger.error("Failed to retrieve clubs by game system: {}", gameSystem, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/location")
    public ResponseEntity<List<Club>> getClubsByLocation(
            @RequestParam String countryCode,
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String city) {
        try {
            logger.debug("Fetching clubs by location: {}, {}, {}", countryCode, provinceCode, city);

            List<Club> clubs = clubService.getClubsByLocation(countryCode, provinceCode, city);

            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUBS_BY_LOCATION",
                    "Viewed clubs for location: " + countryCode +
                            (provinceCode != null ? ", " + provinceCode : "") +
                            (city != null ? ", " + city : ""));

            logger.info("Successfully retrieved {} clubs for location: {}, {}, {}",
                    clubs.size(), countryCode, provinceCode, city);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            loggingService.logError("GET_CLUBS_BY_LOCATION", e,
                    Map.of("countryCode", countryCode,
                            "provinceCode", provinceCode != null ? provinceCode : "null",
                            "city", city != null ? city : "null"));

            logger.error("Failed to retrieve clubs by location: {}, {}, {}",
                    countryCode, provinceCode, city, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Club>> searchClubsByName(@RequestParam String name) {
        try {
            logger.debug("Searching clubs by name: {}", name);

            List<Club> clubs = clubService.searchClubsByName(name);

            loggingService.logUserAction(getCurrentUserId(), "SEARCH_CLUBS",
                    "Searched clubs by name: " + name);

            logger.info("Successfully found {} clubs matching name: {}", clubs.size(), name);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            loggingService.logError("SEARCH_CLUBS_BY_NAME", e,
                    Map.of("searchName", name));

            logger.error("Failed to search clubs by name: {}", name, e);
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