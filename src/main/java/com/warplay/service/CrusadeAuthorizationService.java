package com.warplay.service;

import com.warplay.entity.User;
import com.warplay.entity.UserClub;
import com.warplay.repository.UserClubRepository;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrusadeAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(CrusadeAuthorizationService.class);

    @Autowired
    private UserClubRepository userClubRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Check if a user can create crusades in a club
     * Only club owners and admins can create crusades
     * 
     * @param googleId Google ID (sub claim) of the user
     * @param clubId ID of the club
     * @return true if user has permission, false otherwise
     */
    public boolean canCreateCrusade(String googleId, Long clubId) {
        try {
            // Get user by Google ID
            Optional<User> userOpt = userRepository.findByGoogleId(googleId);
            if (userOpt.isEmpty()) {
                logger.warn("User with Google ID {} not found", googleId);
                return false;
            }
            
            Long userId = userOpt.get().getId();
            
            // Check if user is a member of the club
            Optional<UserClub> membershipOpt = userClubRepository.findByUserIdAndClubId(userId, clubId);
            
            if (membershipOpt.isEmpty()) {
                logger.warn("User {} (Google ID: {}) is not a member of club {}", 
                    userId, googleId, clubId);
                return false;
            }
            
            UserClub membership = membershipOpt.get();
            UserClub.ClubRole role = membership.getRole();
            
            // Only OWNER and ADMIN can create crusades
            boolean hasPermission = role == UserClub.ClubRole.OWNER || role == UserClub.ClubRole.ADMIN;
            
            if (!hasPermission) {
                logger.warn("User {} (Google ID: {}) does not have permission to create crusades in club {}. Role: {}", 
                    userId, googleId, clubId, role);
            }
            
            return hasPermission;
            
        } catch (Exception e) {
            logger.error("Error checking crusade creation permission for user (Google ID: {}) in club {}", 
                googleId, clubId, e);
            return false;
        }
    }

    /**
     * Check if a user can update/delete crusades in a club
     * Only club owners and admins can modify crusades
     * 
     * @param googleId Google ID (sub claim) of the user
     * @param clubId ID of the club
     * @return true if user has permission, false otherwise
     */
    public boolean canModifyCrusade(String googleId, Long clubId) {
        // Same permissions as create for now
        return canCreateCrusade(googleId, clubId);
    }
}
