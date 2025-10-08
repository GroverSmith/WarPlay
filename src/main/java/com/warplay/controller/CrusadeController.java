package com.warplay.controller;

import com.warplay.dto.CreateCrusadeRequest;
import com.warplay.dto.CrusadeResponse;
import com.warplay.dto.UpdateCrusadeRequest;
import com.warplay.service.CrusadeAuthorizationService;
import com.warplay.service.CrusadeService;
import com.warplay.service.LoggingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @Autowired
    private CrusadeAuthorizationService authorizationService;

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
    public ResponseEntity<CrusadeResponse> getCrusade(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching crusade with ID: {}", id);

            Optional<CrusadeResponse> crusade = crusadeService.getActiveCrusadeById(id);

            if (crusade.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                String userId = getUserIdentifier(principal);
                loggingService.logUserAction(userId, "VIEW_CRUSADE",
                        "Viewed crusade: " + crusade.get().getName());
                loggingService.logPerformance("GET_CRUSADE", duration,
                        Map.of("crusadeId", id.toString(), "userId", userId));

                logger.info("User {} successfully retrieved crusade: {}", userId, id);
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
    public ResponseEntity<List<CrusadeResponse>> getCrusadesByClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching crusades for club: {}", clubId);

            List<CrusadeResponse> crusades = crusadeService.getCrusadesByClub(clubId);

            long duration = System.currentTimeMillis() - startTime;
            String userId = getUserIdentifier(principal);
            loggingService.logUserAction(userId, "VIEW_CLUB_CRUSADES",
                    "Viewed crusades for club: " + clubId);
            loggingService.logPerformance("GET_CRUSADES_BY_CLUB", duration,
                    Map.of("clubId", clubId.toString(), "crusadeCount", String.valueOf(crusades.size()), "userId", userId));

            logger.info("User {} successfully retrieved {} crusades for club: {}", userId, crusades.size(), clubId);
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
    public ResponseEntity<List<CrusadeResponse>> getActiveCrusadesByClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active crusades for club: {}", clubId);

            List<CrusadeResponse> crusades = crusadeService.getActiveCrusadesByClub(clubId);

            long duration = System.currentTimeMillis() - startTime;
            String userId = getUserIdentifier(principal);
            loggingService.logUserAction(userId, "VIEW_ACTIVE_CLUB_CRUSADES",
                    "Viewed active crusades for club: " + clubId);
            loggingService.logPerformance("GET_ACTIVE_CRUSADES_BY_CLUB", duration,
                    Map.of("clubId", clubId.toString(), "crusadeCount", String.valueOf(crusades.size()), "userId", userId));

            logger.info("User {} successfully retrieved {} active crusades for club: {}", userId, crusades.size(), clubId);
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
    public ResponseEntity<?> createCrusade(
            @Valid @RequestBody CreateCrusadeRequest request,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            // Check authentication
            if (principal == null) {
                logger.warn("Unauthenticated attempt to create crusade");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required"));
            }

            String googleId = principal.getAttribute("sub");
            String userEmail = principal.getAttribute("email");
            
            if (googleId == null) {
                logger.warn("Google ID (sub) not found in authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User ID not found in token"));
            }

            logger.debug("User {} (Google ID: {}) attempting to create crusade: {}", 
                userEmail, googleId, request.getName());

            // Check authorization - only club admins/owners can create crusades
            if (!authorizationService.canCreateCrusade(googleId, request.getClubId())) {
                logger.warn("User {} (Google ID: {}) does not have permission to create crusades in club {}", 
                    userEmail, googleId, request.getClubId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You do not have permission to create crusades in this club. Only club administrators can create crusades."));
            }

            // Create the crusade
            CrusadeResponse savedCrusade = crusadeService.createCrusade(request);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(userEmail != null ? userEmail : googleId, "CREATE_CRUSADE",
                    "Created crusade: " + savedCrusade.getName() + " in club: " + request.getClubId());
            loggingService.logPerformance("CREATE_CRUSADE", duration,
                    Map.of("crusadeId", savedCrusade.getId().toString(),
                            "clubId", request.getClubId().toString(),
                            "googleId", googleId));

            logger.info("User {} (Google ID: {}) successfully created crusade: {} (ID: {}) in club: {}",
                    userEmail, googleId, savedCrusade.getName(), savedCrusade.getId(), request.getClubId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCrusade);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CRUSADE_VALIDATION", e,
                    Map.of("crusadeName", request.getName(),
                            "clubId", request.getClubId().toString(),
                            "duration", String.valueOf(duration)));

            logger.warn("Crusade creation failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CREATE_CRUSADE", e,
                    Map.of("crusadeName", request.getName(),
                            "clubId", request.getClubId().toString(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to create crusade: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to create crusade"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCrusade(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCrusadeRequest request,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            // Check authentication
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required"));
            }

            String googleId = principal.getAttribute("sub");
            String userEmail = principal.getAttribute("email");
            
            if (googleId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User ID not found in token"));
            }

            logger.debug("User {} (Google ID: {}) attempting to update crusade: {}", 
                userEmail, googleId, id);

            // Get the existing crusade to check club membership
            Optional<CrusadeResponse> existingCrusade = crusadeService.getActiveCrusadeById(id);
            if (existingCrusade.isEmpty()) {
                logger.warn("Active crusade not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

            Long clubId = existingCrusade.get().getClubId();

            // Check authorization
            if (!authorizationService.canModifyCrusade(googleId, clubId)) {
                logger.warn("User {} (Google ID: {}) does not have permission to update crusade {} in club {}", 
                    userEmail, googleId, id, clubId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You do not have permission to modify this crusade"));
            }

            // Update the crusade
            Optional<CrusadeResponse> updatedCrusade = crusadeService.updateCrusade(id, request);

            if (updatedCrusade.isPresent()) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userEmail != null ? userEmail : googleId, "UPDATE_CRUSADE",
                        "Updated crusade: " + updatedCrusade.get().getName());
                loggingService.logPerformance("UPDATE_CRUSADE", duration,
                        Map.of("crusadeId", id.toString(), "googleId", googleId));

                logger.info("User {} (Google ID: {}) successfully updated crusade: {}", 
                    userEmail, googleId, id);
                return ResponseEntity.ok(updatedCrusade.get());
            } else {
                logger.warn("Active crusade not found for update: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CRUSADE_VALIDATION", e,
                    Map.of("crusadeId", id.toString(),
                            "duration", String.valueOf(duration)));

            logger.warn("Crusade update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("UPDATE_CRUSADE", e,
                    Map.of("crusadeId", id.toString(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to update crusade: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update crusade"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCrusade(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        long startTime = System.currentTimeMillis();

        try {
            // Check authentication
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required"));
            }

            String googleId = principal.getAttribute("sub");
            String userEmail = principal.getAttribute("email");
            
            if (googleId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User ID not found in token"));
            }

            logger.debug("User {} (Google ID: {}) attempting to delete crusade: {}", 
                userEmail, googleId, id);

            // Get the existing crusade to check club membership
            Optional<CrusadeResponse> existingCrusade = crusadeService.getActiveCrusadeById(id);
            if (existingCrusade.isEmpty()) {
                logger.warn("Active crusade not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

            Long clubId = existingCrusade.get().getClubId();

            // Check authorization
            if (!authorizationService.canModifyCrusade(googleId, clubId)) {
                logger.warn("User {} (Google ID: {}) does not have permission to delete crusade {} in club {}", 
                    userEmail, googleId, id, clubId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "You do not have permission to delete this crusade"));
            }

            // Delete the crusade
            boolean deleted = crusadeService.softDeleteCrusade(id);

            if (deleted) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logUserAction(userEmail != null ? userEmail : googleId, "DELETE_CRUSADE",
                        "Deleted crusade ID: " + id);
                loggingService.logPerformance("DELETE_CRUSADE", duration,
                        Map.of("crusadeId", id.toString(), "googleId", googleId));

                logger.info("User {} (Google ID: {}) successfully soft deleted crusade: {}", 
                    userEmail, googleId, id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Active crusade not found for deletion: {}", id);
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("DELETE_CRUSADE", e,
                    Map.of("crusadeId", id.toString(),
                            "duration", String.valueOf(duration)));

            logger.error("Failed to delete crusade: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete crusade"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CrusadeResponse>> searchCrusadesByName(
            @RequestParam String name,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            logger.debug("Searching crusades by name: {}", name);

            List<CrusadeResponse> crusades = crusadeService.searchCrusadesByName(name);

            String userId = getUserIdentifier(principal);
            loggingService.logUserAction(userId, "SEARCH_CRUSADES",
                    "Searched crusades by name: " + name);

            logger.info("User {} successfully found {} crusades matching name: {}", userId, crusades.size(), name);
            return ResponseEntity.ok(crusades);

        } catch (Exception e) {
            loggingService.logError("SEARCH_CRUSADES_BY_NAME", e,
                    Map.of("searchName", name));

            logger.error("Failed to search crusades by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user identifier for logging purposes
     * Preference: email > name > Google ID > "anonymous"
     * 
     * @param principal OAuth2User principal (can be null for public endpoints)
     * @return User identifier string for logging
     */
    private String getUserIdentifier(OAuth2User principal) {
        if (principal == null) {
            return "anonymous";
        }
        
        // Prefer email as it's unique and readable
        String email = principal.getAttribute("email");
        if (email != null && !email.isEmpty()) {
            return email;
        }
        
        // Next prefer name for readability
        String name = principal.getAttribute("name");
        if (name != null && !name.isEmpty()) {
            return name;
        }
        
        // Fallback to Google ID if name not available
        String googleId = principal.getAttribute("sub");
        if (googleId != null && !googleId.isEmpty()) {
            return "googleId:" + googleId;
        }
        
        return "anonymous";
    }

}
