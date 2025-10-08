package com.warplay.controller;

import com.warplay.dto.CreateCrusadeRequest;
import com.warplay.dto.CrusadeResponse;
import com.warplay.dto.UpdateCrusadeRequest;
import com.warplay.service.CrusadeService;
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
@RequestMapping("/api/crusades")
public class CrusadeController {

    private static final Logger logger = LoggerFactory.getLogger(CrusadeController.class);

    @Autowired
    private CrusadeService crusadeService;

    @Autowired
    private LoggingService loggingService;

    @GetMapping
    public ResponseEntity<List<CrusadeResponse>> getAllActiveCrusades() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active crusades");

            List<CrusadeResponse> crusades = crusadeService.getAllActiveCrusades();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logPerformance("GET_ALL_CRUSADES", duration,
                    Map.of("crusadeCount", String.valueOf(crusades.size())));

            logger.info("Successfully retrieved {} active crusades", crusades.size());
            return ResponseEntity.ok(crusades);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ALL_CRUSADES", e,
                    Map.of("duration", String.valueOf(duration)));

            logger.error("Failed to retrieve crusades", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrusadeResponse> getCrusade(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching crusade with ID: {}", id);

            Optional<CrusadeResponse> crusade = crusadeService.getActiveCrusadeById(id);

            if (crusade.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(getCurrentUserId(), "VIEW_CRUSADE",
                        "Viewed crusade: " + crusade.get().getName());
                loggingService.logPerformance("GET_CRUSADE", duration,
                        Map.of("crusadeId", id.toString()));

                logger.info("Successfully retrieved crusade: {}", id);
                return ResponseEntity.ok(crusade.get());
            } else {
                logger.warn("Active crusade not found: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_CRUSADE", e,
                    Map.of("crusadeId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve crusade: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<CrusadeResponse>> getCrusadesByClub(@PathVariable Long clubId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching crusades for club: {}", clubId);

            List<CrusadeResponse> crusades = crusadeService.getCrusadesByClub(clubId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUB_CRUSADES",
                    "Viewed crusades for club: " + clubId);
            loggingService.logPerformance("GET_CRUSADES_BY_CLUB", duration,
                    Map.of("clubId", clubId.toString(), "crusadeCount", String.valueOf(crusades.size())));

            logger.info("Successfully retrieved {} crusades for club: {}", crusades.size(), clubId);
            return ResponseEntity.ok(crusades);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_CRUSADES_BY_CLUB", e,
                    Map.of("clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve crusades for club: {}", clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/club/{clubId}/active")
    public ResponseEntity<List<CrusadeResponse>> getActiveCrusadesByClub(@PathVariable Long clubId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active crusades for club: {}", clubId);

            List<CrusadeResponse> crusades = crusadeService.getActiveCrusadesByClub(clubId);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_ACTIVE_CLUB_CRUSADES",
                    "Viewed active crusades for club: " + clubId);
            loggingService.logPerformance("GET_ACTIVE_CRUSADES_BY_CLUB", duration,
                    Map.of("clubId", clubId.toString(), "crusadeCount", String.valueOf(crusades.size())));

            logger.info("Successfully retrieved {} active crusades for club: {}", crusades.size(), clubId);
            return ResponseEntity.ok(crusades);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_ACTIVE_CRUSADES_BY_CLUB", e,
                    Map.of("clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve active crusades for club: {}", clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<CrusadeResponse> createCrusade(@Valid @RequestBody CreateCrusadeRequest request) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Creating new crusade: {}", request.getName());

            CrusadeResponse savedCrusade = crusadeService.createCrusade(request);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(userId, "CREATE_CRUSADE",
                    "Created crusade: " + savedCrusade.getName());
            loggingService.logPerformance("CREATE_CRUSADE", duration,
                    Map.of("crusadeId", savedCrusade.getId().toString()));

            logger.info("Successfully created crusade: {} (ID: {})",
                    savedCrusade.getName(), savedCrusade.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCrusade);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CRUSADE_VALIDATION", e,
                    Map.of("userId", userId, "crusadeName", request.getName(),
                            "duration", String.valueOf(duration)));

            logger.warn("Crusade creation failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CRUSADE", e,
                    Map.of("userId", userId, "crusadeName", request.getName(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create crusade: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrusadeResponse> updateCrusade(@PathVariable Long id, @Valid @RequestBody UpdateCrusadeRequest request) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Updating crusade: {}", id);

            Optional<CrusadeResponse> updatedCrusade = crusadeService.updateCrusade(id, request);

            if (updatedCrusade.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userId, "UPDATE_CRUSADE",
                        "Updated crusade: " + updatedCrusade.get().getName());
                loggingService.logPerformance("UPDATE_CRUSADE", duration,
                        Map.of("crusadeId", id.toString()));

                logger.info("Successfully updated crusade: {}", id);
                return ResponseEntity.ok(updatedCrusade.get());
            } else {
                logger.warn("Active crusade not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CRUSADE_VALIDATION", e,
                    Map.of("crusadeId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.warn("Crusade update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CRUSADE", e,
                    Map.of("crusadeId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update crusade: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrusade(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();

        try {
            logger.debug("Soft deleting crusade: {}", id);

            boolean deleted = crusadeService.softDeleteCrusade(id);

            if (deleted) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userId, "DELETE_CRUSADE",
                        "Deleted crusade ID: " + id);
                loggingService.logPerformance("DELETE_CRUSADE", duration,
                        Map.of("crusadeId", id.toString()));

                logger.info("Successfully soft deleted crusade: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Active crusade not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DELETE_CRUSADE", e,
                    Map.of("crusadeId", id.toString(), "userId", userId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to delete crusade: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CrusadeResponse>> searchCrusadesByName(@RequestParam String name) {
        try {
            logger.debug("Searching crusades by name: {}", name);

            List<CrusadeResponse> crusades = crusadeService.searchCrusadesByName(name);

            loggingService.logUserAction(getCurrentUserId(), "SEARCH_CRUSADES",
                    "Searched crusades by name: " + name);

            logger.info("Successfully found {} crusades matching name: {}", crusades.size(), name);
            return ResponseEntity.ok(crusades);

        } catch (Exception e) {
            loggingService.logError("SEARCH_CRUSADES_BY_NAME", e,
                    Map.of("searchName", name));

            logger.error("Failed to search crusades by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getCurrentUserId() {
        // Implement based on your authentication mechanism
        // This is a placeholder - adjust based on your security setup
        return "system";
    }
}
