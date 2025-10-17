package com.warplay.service;

import com.warplay.entity.User;
import com.warplay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SessionTokenService {
    private static final Logger logger = LoggerFactory.getLogger(SessionTokenService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Validate JWT session token and return the associated user
     * @param sessionToken The JWT session token to validate
     * @return Optional containing the user if token is valid, empty otherwise
     */
    public Optional<User> validateSessionToken(String sessionToken) {
        try {
            if (sessionToken == null) {
                logger.debug("Session token is null");
                return Optional.empty();
            }

            // Validate JWT token
            Optional<Map<String, Object>> userInfo = jwtService.validateToken(sessionToken);
            if (userInfo.isPresent()) {
                Long userId = (Long) userInfo.get().get("userId");
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    logger.debug("Valid JWT session token for user: {}", user.get().getEmail());
                    return user;
                } else {
                    logger.debug("User not found for JWT token userId: {}", userId);
                    return Optional.empty();
                }
            }

            logger.debug("Invalid JWT session token");
            return Optional.empty();
        } catch (Exception e) {
            logger.debug("Error validating JWT session token: {}", sessionToken, e);
            return Optional.empty();
        }
    }

    /**
     * Get user ID from session token
     * @param sessionToken The session token
     * @return User ID if token is valid, null otherwise
     */
    public Long getUserIdFromToken(String sessionToken) {
        Optional<User> user = validateSessionToken(sessionToken);
        return user.map(User::getId).orElse(null);
    }

    /**
     * Generate a new session token for a user
     * @param user The user to generate a token for
     * @return JWT session token
     */
    public String generateSessionToken(User user) {
        return jwtService.generateToken(user);
    }
}
