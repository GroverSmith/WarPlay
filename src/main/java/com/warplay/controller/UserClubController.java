package com.warplay.controller;

import com.warplay.dto.ClubMembershipResponse;
import com.warplay.entity.UserClub;
import com.warplay.entity.UserClub.ClubRole;
import com.warplay.service.ClubService;
import com.warplay.service.UserClubService;
import com.warplay.service.UserClubService.JoinClubRequest;
import com.warplay.service.UserClubService.ChangeRoleRequest;
import com.warplay.service.LoggingService;
import com.warplay.service.UserService;
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

@RestController
@RequestMapping("/api/user-clubs")
public class UserClubController {

    private static final Logger logger = LoggerFactory.getLogger(UserClubController.class);

    @Autowired
    private UserClubService userClubService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    /**
     * Join a club using authenticated user from Google OAuth
     * POST /api/user-clubs/join
     * Authorization: Bearer <google-token>
     * Body: { "clubId": 5 }
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinClub(
            @RequestBody JoinClubRequest request,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            // Extract user email from Google OAuth token
            String email = principal.getAttribute("email");

            if (email == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ClubMembershipResponse("User email not found in token"));
            }

            // Find or create user by email
            Long userId = userService.getUserIdByEmail(email);

            // Join the club
            UserClub response = userClubService.joinClub(userId, request.getClubId());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ClubMembershipResponse(e.getMessage()));
        }
    }

    /**
     * Alternative endpoint: Join club using path variable
     * POST /api/user-clubs/join/{clubId}
     */
    @PostMapping("/join/{clubId}")
    public ResponseEntity<?> joinClubByPath(
            @PathVariable Long clubId,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            String email = principal.getAttribute("email");

            if (email == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ClubMembershipResponse("User email not found in token"));
            }

            Long userId = userService.getUserIdByEmail(email);
            UserClub response = userClubService.joinClub(userId, clubId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ClubMembershipResponse(e.getMessage()));
        }
    }



    // Leave a club
    @PostMapping("/leave/{userId}/{clubId}")
    public ResponseEntity<Void> leaveClub(@PathVariable Long userId, @PathVariable Long clubId) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("User {} leaving club {}", userId, clubId);

            boolean left = userClubService.leaveClub(userId, clubId);

            if (left) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logPerformance("LEAVE_CLUB", duration,
                        Map.of("userId", userId.toString(), "clubId", clubId.toString()));

                logger.info("Successfully left club: user {} - club {}", userId, clubId);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Active membership not found for leaving: user {} - club {}", userId, clubId);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalStateException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("LEAVE_CLUB_STATE", e,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString(),
                            "duration", String.valueOf(duration)));

            logger.warn("Club leave failed - state error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("LEAVE_CLUB", e,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to leave club: user {} - club {}", userId, clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Change user role in club
    @PutMapping("/role/{clubId}")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long clubId, @Valid @RequestBody ChangeRoleRequest request) {
        long startTime = System.currentTimeMillis();
        String currentUserId = getCurrentUserId();

        try {
            logger.debug("Changing user {} role in club {} to {}",
                    request.getTargetUserId(), clubId, request.getNewRole());

            // Use current user as admin (in real app, get from security context)
            Long adminUserId = Long.parseLong(currentUserId); // Simplified for demo

            boolean changed = userClubService.changeUserRole(
                    adminUserId,
                    request.getTargetUserId(),
                    clubId,
                    request.getNewRole()
            );

            if (changed) {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logPerformance("CHANGE_USER_ROLE", duration,
                        Map.of("adminUserId", adminUserId.toString(),
                                "targetUserId", request.getTargetUserId().toString(),
                                "clubId", clubId.toString(),
                                "newRole", request.getNewRole().toString()));

                logger.info("Successfully changed user role: user {} in club {} to {}",
                        request.getTargetUserId(), clubId, request.getNewRole());
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Membership not found for role change: user {} - club {}",
                        request.getTargetUserId(), clubId);
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CHANGE_USER_ROLE_VALIDATION", e,
                    Map.of("targetUserId", request.getTargetUserId().toString(),
                            "clubId", clubId.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.warn("Role change failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (IllegalStateException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CHANGE_USER_ROLE_STATE", e,
                    Map.of("targetUserId", request.getTargetUserId().toString(),
                            "clubId", clubId.toString(),
                            "duration", String.valueOf(duration)));

            logger.warn("Role change failed - state error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("CHANGE_USER_ROLE", e,
                    Map.of("targetUserId", request.getTargetUserId().toString(),
                            "clubId", clubId.toString(),
                            "currentUserId", currentUserId,
                            "duration", String.valueOf(duration)));

            logger.error("Failed to change user role: user {} in club {}",
                    request.getTargetUserId(), clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get user's clubs
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserClub>> getUserClubs(@PathVariable Long userId,
                                                       @RequestParam(defaultValue = "true") boolean activeOnly) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching clubs for user: {} (active only: {})", userId, activeOnly);

            List<UserClub> clubs = userClubService.getUserClubs(userId, activeOnly);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_USER_CLUBS",
                    "Viewed clubs for user: " + userId);
            loggingService.logPerformance("GET_USER_CLUBS", duration,
                    Map.of("userId", userId.toString(),
                            "activeOnly", String.valueOf(activeOnly),
                            "clubCount", String.valueOf(clubs.size())));

            logger.info("Successfully retrieved {} clubs for user: {}", clubs.size(), userId);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER_CLUBS", e,
                    Map.of("userId", userId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve clubs for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get club members
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<UserClub>> getClubMembers(@PathVariable Long clubId,
                                                         @RequestParam(defaultValue = "true") boolean activeOnly) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching members for club: {} (active only: {})", clubId, activeOnly);

            List<UserClub> members = userClubService.getClubMembers(clubId, activeOnly);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUB_MEMBERS",
                    "Viewed members for club: " + clubId);
            loggingService.logPerformance("GET_CLUB_MEMBERS", duration,
                    Map.of("clubId", clubId.toString(),
                            "activeOnly", String.valueOf(activeOnly),
                            "memberCount", String.valueOf(members.size())));

            logger.info("Successfully retrieved {} members for club: {}", members.size(), clubId);
            return ResponseEntity.ok(members);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_CLUB_MEMBERS", e,
                    Map.of("clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve members for club: {}", clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get club members by role
    @GetMapping("/club/{clubId}/role/{role}")
    public ResponseEntity<List<UserClub>> getClubMembersByRole(@PathVariable Long clubId,
                                                               @PathVariable ClubRole role,
                                                               @RequestParam(defaultValue = "true") boolean activeOnly) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching members for club: {} with role: {} (active only: {})", clubId, role, activeOnly);

            List<UserClub> members = userClubService.getClubMembersByRole(clubId, role, activeOnly);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_CLUB_MEMBERS_BY_ROLE",
                    "Viewed " + role + " members for club: " + clubId);
            loggingService.logPerformance("GET_CLUB_MEMBERS_BY_ROLE", duration,
                    Map.of("clubId", clubId.toString(),
                            "role", role.toString(),
                            "activeOnly", String.valueOf(activeOnly),
                            "memberCount", String.valueOf(members.size())));

            logger.info("Successfully retrieved {} {} members for club: {}", members.size(), role, clubId);
            return ResponseEntity.ok(members);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_CLUB_MEMBERS_BY_ROLE", e,
                    Map.of("clubId", clubId.toString(), "role", role.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve {} members for club: {}", role, clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get user's clubs by game system
    @GetMapping("/user/{userId}/game-system/{gameSystem}")
    public ResponseEntity<List<UserClub>> getUserClubsByGameSystem(@PathVariable Long userId,
                                                                   @PathVariable String gameSystem) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching clubs for user: {} and game system: {}", userId, gameSystem);

            List<UserClub> clubs = userClubService.getUserClubsByGameSystem(userId, gameSystem);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logUserAction(getCurrentUserId(), "VIEW_USER_CLUBS_BY_GAME_SYSTEM",
                    "Viewed clubs for user: " + userId + " and game system: " + gameSystem);
            loggingService.logPerformance("GET_USER_CLUBS_BY_GAME_SYSTEM", duration,
                    Map.of("userId", userId.toString(),
                            "gameSystem", gameSystem,
                            "clubCount", String.valueOf(clubs.size())));

            logger.info("Successfully retrieved {} clubs for user: {} and game system: {}",
                    clubs.size(), userId, gameSystem);
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logError("GET_USER_CLUBS_BY_GAME_SYSTEM", e,
                    Map.of("userId", userId.toString(), "gameSystem", gameSystem, "duration", String.valueOf(duration)));

            logger.error("Failed to retrieve clubs for user: {} and game system: {}", userId, gameSystem, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Check if user is member of club
    @GetMapping("/check-membership/{userId}/{clubId}")
    public ResponseEntity<Map<String, Object>> checkMembership(@PathVariable Long userId, @PathVariable Long clubId) {
        try {
            logger.debug("Checking membership: user {} - club {}", userId, clubId);

            boolean isMember = userClubService.isUserMemberOfClub(userId, clubId);
            boolean isAdmin = userClubService.isUserAdminOfClub(userId, clubId);
            boolean isOwner = userClubService.isUserOwnerOfClub(userId, clubId);

            Map<String, Object> membershipInfo = Map.of(
                    "isMember", isMember,
                    "isAdmin", isAdmin,
                    "isOwner", isOwner
            );

            loggingService.logUserAction(getCurrentUserId(), "CHECK_MEMBERSHIP",
                    "Checked membership for user: " + userId + " - club: " + clubId);

            logger.info("Membership check: user {} - club {} = member: {}, admin: {}, owner: {}",
                    userId, clubId, isMember, isAdmin, isOwner);

            return ResponseEntity.ok(membershipInfo);

        } catch (Exception e) {
            loggingService.logError("CHECK_MEMBERSHIP", e,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString()));

            logger.error("Failed to check membership: user {} - club {}", userId, clubId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get clubs owned by user
    @GetMapping("/user/{userId}/owned")
    public ResponseEntity<List<UserClub>> getClubsOwnedByUser(@PathVariable Long userId) {
        try {
            logger.debug("Fetching clubs owned by user: {}", userId);

            List<UserClub> ownedClubs = userClubService.getClubsOwnedByUser(userId);

            loggingService.logUserAction(getCurrentUserId(), "VIEW_OWNED_CLUBS",
                    "Viewed clubs owned by user: " + userId);

            logger.info("Successfully retrieved {} clubs owned by user: {}", ownedClubs.size(), userId);
            return ResponseEntity.ok(ownedClubs);

        } catch (Exception e) {
            loggingService.logError("GET_CLUBS_OWNED_BY_USER", e,
                    Map.of("userId", userId.toString()));

            logger.error("Failed to retrieve clubs owned by user: {}", userId, e);
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
        return "1"; // Default to user ID 1 for demo
    }
}