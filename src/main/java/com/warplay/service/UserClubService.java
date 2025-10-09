package com.warplay.service;

import com.warplay.entity.UserClub;
import com.warplay.entity.UserClub.ClubRole;
import com.warplay.entity.Club;
import com.warplay.entity.User;
import com.warplay.repository.UserClubRepository;
import com.warplay.repository.ClubRepository;
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
public class UserClubService {

    private static final Logger logger = LoggerFactory.getLogger(UserClubService.class);

    @Autowired
    private UserClubRepository userClubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private LoggingService loggingService;

    public UserClub joinClub(Long userId, Long clubId) {
        return joinClub(userId, clubId, ClubRole.MEMBER);
    }

    public UserClub joinClub(Long userId, Long clubId, ClubRole role) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("User {} joining club {} with role {}", userId, clubId, role);

            // Validate user and club exist
            Optional<User> user = userRepository.findByIdActive(userId);
            Optional<Club> club = clubRepository.findByIdAndDeletedTimestampIsNull(clubId);

            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }
            if (club.isEmpty()) {
                throw new IllegalArgumentException("Club not found with ID: " + clubId);
            }

            // Check if user is already a member
            Optional<UserClub> existingMembership = userClubRepository.findByUserIdAndClubId(userId, clubId);

            if (existingMembership.isPresent() && existingMembership.get().isActive()) {
                throw new IllegalArgumentException("User is already an active member of this club");
            }

            UserClub userClub;
            boolean isRejoin = false;

            if (existingMembership.isPresent()) {
                // Reactivate existing membership
                userClub = existingMembership.get();
                userClub.rejoinClub();
                userClub.setRole(role != null ? role : ClubRole.MEMBER);
                isRejoin = true;
            } else {
                // Create new membership
                userClub = new UserClub(user.get(), club.get(), role != null ? role : ClubRole.MEMBER);
            }

            UserClub savedMembership = userClubRepository.save(userClub);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_clubs", isRejoin ? "REJOIN" : "INSERT", true,
                    (isRejoin ? "Rejoined" : "Joined") + " club: " + club.get().getName());
            loggingService.logUserAction(userId.toString(), isRejoin ? "REJOIN_CLUB" : "JOIN_CLUB",
                    (isRejoin ? "Rejoined" : "Joined") + " club: " + club.get().getName() + " as " + role);
            loggingService.logPerformance("DB_JOIN_CLUB", duration,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString(), "role", role.toString()));

            logger.info("Successfully {} club: user {} - club {} with role {}",
                    isRejoin ? "rejoined" : "joined", userId, clubId, role);

            return savedMembership;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_clubs", "JOIN", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_JOIN_CLUB", e,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to join club: user {} - club {}", userId, clubId, e);
            throw new RuntimeException("Failed to join club", e);
        }
    }

    public boolean leaveClub(Long userId, Long clubId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("User {} leaving club {}", userId, clubId);

            Optional<UserClub> membershipOpt = userClubRepository.findByUserIdAndClubIdAndIsActiveTrue(userId, clubId);

            if (membershipOpt.isPresent()) {
                UserClub membership = membershipOpt.get();

                // Check if user is the owner and there are other members
                if (membership.getRole() == ClubRole.OWNER) {
                    Long activeMemberCount = userClubRepository.countActiveMembersByClubId(clubId);
                    if (activeMemberCount > 1) {
                        throw new IllegalStateException("Club owner cannot leave while other members exist. " +
                                "Transfer ownership first or remove all other members.");
                    }
                }

                membership.leaveClub();
                userClubRepository.save(membership);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_clubs", "LEAVE", true,
                        "Left club: " + membership.getClub().getName());
                loggingService.logUserAction(userId.toString(), "LEAVE_CLUB",
                        "Left club: " + membership.getClub().getName());
                loggingService.logPerformance("DB_LEAVE_CLUB", duration,
                        Map.of("userId", userId.toString(), "clubId", clubId.toString()));

                logger.info("Successfully left club: user {} - club {}", userId, clubId);
                return true;
            } else {
                logger.warn("Active membership not found: user {} - club {}", userId, clubId);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_clubs", "LEAVE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_LEAVE_CLUB", e,
                    Map.of("userId", userId.toString(), "clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to leave club: user {} - club {}", userId, clubId, e);
            throw new RuntimeException("Failed to leave club", e);
        }
    }

    public boolean changeUserRole(Long adminUserId, Long targetUserId, Long clubId, ClubRole newRole) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Changing user {} role in club {} to {} by admin {}",
                    targetUserId, clubId, newRole, adminUserId);

            // Validate admin permissions
            if (!userClubRepository.isUserAdminOfClub(adminUserId, clubId)) {
                throw new IllegalArgumentException("User does not have admin privileges for this club");
            }

            // Find target user membership
            Optional<UserClub> membershipOpt = userClubRepository.findByUserIdAndClubIdAndIsActiveTrue(targetUserId, clubId);

            if (membershipOpt.isPresent()) {
                UserClub membership = membershipOpt.get();
                ClubRole oldRole = membership.getRole();

                // Prevent demoting the only owner
                if (oldRole == ClubRole.OWNER && newRole != ClubRole.OWNER) {
                    Long ownerCount = userClubRepository.countActiveMembersByClubIdAndRole(clubId, ClubRole.OWNER);
                    if (ownerCount <= 1) {
                        throw new IllegalStateException("Cannot demote the only owner. Promote another member first.");
                    }
                }

                membership.setRole(newRole);
                userClubRepository.save(membership);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("user_clubs", "CHANGE_ROLE", true,
                        "Changed role from " + oldRole + " to " + newRole);
                loggingService.logUserAction(adminUserId.toString(), "CHANGE_MEMBER_ROLE",
                        "Changed user " + targetUserId + " role from " + oldRole + " to " + newRole +
                                " in club " + membership.getClub().getName());
                loggingService.logPerformance("DB_CHANGE_USER_ROLE", duration,
                        Map.of("adminUserId", adminUserId.toString(), "targetUserId", targetUserId.toString(),
                                "clubId", clubId.toString(), "newRole", newRole.toString()));

                logger.info("Successfully changed user role: user {} in club {} from {} to {}",
                        targetUserId, clubId, oldRole, newRole);
                return true;
            } else {
                logger.warn("Active membership not found for role change: user {} - club {}", targetUserId, clubId);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("user_clubs", "CHANGE_ROLE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_CHANGE_USER_ROLE", e,
                    Map.of("adminUserId", adminUserId.toString(), "targetUserId", targetUserId.toString(),
                            "clubId", clubId.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to change user role: user {} in club {}", targetUserId, clubId, e);
            throw new RuntimeException("Failed to change user role", e);
        }
    }

    // Query methods
    public List<UserClub> getUserClubs(Long userId, boolean activeOnly) {
        logger.debug("Fetching clubs for user: {} (active only: {})", userId, activeOnly);
        return activeOnly ?
                userClubRepository.findByUserIdAndIsActiveTrue(userId) :
                userClubRepository.findByUserId(userId);
    }

    public List<UserClub> getClubMembers(Long clubId, boolean activeOnly) {
        logger.debug("Fetching members for club: {} (active only: {})", clubId, activeOnly);
        return activeOnly ?
                userClubRepository.findByClubIdAndIsActiveTrueWithUser(clubId) :
                userClubRepository.findByClubId(clubId);
    }

    public List<UserClub> getClubMembersByRole(Long clubId, ClubRole role, boolean activeOnly) {
        logger.debug("Fetching members for club: {} with role: {} (active only: {})", clubId, role, activeOnly);
        return activeOnly ?
                userClubRepository.findByClubIdAndRoleAndIsActiveTrue(clubId, role) :
                userClubRepository.findByClubIdAndRole(clubId, role);
    }

    public List<UserClub> getUserClubsByGameSystem(Long userId, String gameSystem) {
        logger.debug("Fetching clubs for user: {} and game system: {}", userId, gameSystem);
        return userClubRepository.findActiveClubsByUserAndGameSystem(userId, gameSystem);
    }

    public List<UserClub> getUsersByLocation(String countryCode, String provinceCode, String city) {
        logger.debug("Fetching users by location: {}, {}, {}", countryCode, provinceCode, city);
        return userClubRepository.findActiveUsersByLocation(countryCode, provinceCode, city);
    }

    // Permission checking methods
    public boolean isUserMemberOfClub(Long userId, Long clubId) {
        return userClubRepository.isUserMemberOfClub(userId, clubId);
    }

    public boolean isUserAdminOfClub(Long userId, Long clubId) {
        return userClubRepository.isUserAdminOfClub(userId, clubId);
    }

    public boolean isUserOwnerOfClub(Long userId, Long clubId) {
        return userClubRepository.isUserOwnerOfClub(userId, clubId);
    }

    // Statistics methods
    public Long getActiveMemberCount(Long clubId) {
        return userClubRepository.countActiveMembersByClubId(clubId);
    }

    public List<UserClub> getClubsOwnedByUser(Long userId) {
        logger.debug("Fetching clubs owned by user: {}", userId);
        return userClubRepository.findClubsOwnedByUser(userId);
    }

    public List<UserClub> getClubsAdministeredByUser(Long userId) {
        logger.debug("Fetching clubs administered by user: {}", userId);
        return userClubRepository.findClubsAdministeredByUser(userId);
    }

    private void validateJoinClubRequest(Long userId, Long clubId, ClubRole role) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (clubId == null) {
            throw new IllegalArgumentException("Club ID cannot be null");
        }

        if (role == null) {
            role = ClubRole.MEMBER; // Default role
        }

        logger.debug("Join club validation passed for user {} - club {} with role {}", userId, clubId, role);
    }

    // DTO classes for requests
    public static class JoinClubRequest {
        private Long userId;
        private Long clubId;
        private ClubRole role = ClubRole.MEMBER;
        private String notes;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getClubId() { return clubId; }
        public void setClubId(Long clubId) { this.clubId = clubId; }

        public ClubRole getRole() { return role; }
        public void setRole(ClubRole role) { this.role = role; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ChangeRoleRequest {
        private Long targetUserId;
        private ClubRole newRole;

        // Getters and setters
        public Long getTargetUserId() { return targetUserId; }
        public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

        public ClubRole getNewRole() { return newRole; }
        public void setNewRole(ClubRole newRole) { this.newRole = newRole; }
    }
}