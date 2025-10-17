package com.warplay.service;

import com.warplay.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // JWT secret key from environment variable
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    /**
     * Validate that JWT secret is properly configured
     */
    @PostConstruct
    public void validateConfiguration() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable must be set");
        }
        if (secretKey.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long for security");
        }
        logger.info("JWT service initialized with secret key length: {}", secretKey.length());
    }

    /**
     * Generate a JWT token for the given user
     * @param user The user to create a token for
     * @return JWT token string
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generate a JWT token with additional claims
     * @param extraClaims Additional claims to include in the token
     * @param user The user to create a token for
     * @return JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, User user) {
        try {
            Map<String, Object> claims = new HashMap<>(extraClaims);
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("name", user.getName());
            claims.put("googleId", user.getGoogleId());
            
            // Add roles/permissions if needed in the future
            claims.put("roles", "USER"); // For now, all users have USER role
            
            return buildToken(claims, user.getEmail(), jwtExpiration);
        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Build a JWT token with the given claims
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer("warplay-api")
                .setAudience("warplay-client")
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username (email) from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from JWT token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract a specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get the signing key for JWT
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Check if JWT token is valid (not expired and valid signature)
     */
    public boolean isTokenValid(String token, String userEmail) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userEmail)) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if JWT token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration date from JWT token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validate JWT token and return user information
     * @param token JWT token to validate
     * @return Optional containing user info if valid, empty otherwise
     */
    public Optional<Map<String, Object>> validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            
            // Check if token is expired
            if (isTokenExpired(token)) {
                logger.debug("Token is expired");
                return Optional.empty();
            }
            
            // Extract user information
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", claims.get("userId"));
            userInfo.put("email", claims.get("email"));
            userInfo.put("name", claims.get("name"));
            userInfo.put("googleId", claims.get("googleId"));
            userInfo.put("roles", claims.get("roles"));
            
            return Optional.of(userInfo);
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
