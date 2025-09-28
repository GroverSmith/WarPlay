package com.warplay.repository;

import com.warplay.entity.UserGameSystem;
import com.warplay.entity.User;
import com.warplay.entity.GameSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGameSystemRepository extends JpaRepository<UserGameSystem, Long> {

    // Find by user and game system
    Optional<UserGameSystem> findByUserAndGameSystem(User user, GameSystem gameSystem);

    // Find by user ID and game system ID
    Optional<UserGameSystem> findByUserIdAndGameSystemId(Long userId, Long gameSystemId);

    // Find all game systems for a user
    List<UserGameSystem> findByUser(User user);

    // Find all game systems for a user ID
    List<UserGameSystem> findByUserId(Long userId);

    // Find all active game systems for a user
    @Query("SELECT ugs FROM UserGameSystem ugs WHERE ugs.user.id = ?1 AND ugs.isActive = true")
    List<UserGameSystem> findActiveByUserId(Long userId);

    // Find all users for a game system
    List<UserGameSystem> findByGameSystem(GameSystem gameSystem);

    // Find all users for a game system ID
    List<UserGameSystem> findByGameSystemId(Long gameSystemId);

    // Find all active users for a game system
    @Query("SELECT ugs FROM UserGameSystem ugs WHERE ugs.gameSystem.id = ?1 AND ugs.isActive = true")
    List<UserGameSystem> findActiveByGameSystemId(Long gameSystemId);

    // Find users by skill rating range for a game system
    @Query("SELECT ugs FROM UserGameSystem ugs WHERE ugs.gameSystem.id = ?1 AND ugs.skillRating BETWEEN ?2 AND ?3 AND ugs.isActive = true")
    List<UserGameSystem> findByGameSystemIdAndSkillRatingBetween(Long gameSystemId, Integer minRating, Integer maxRating);

    // Find users by years experience for a game system
    @Query("SELECT ugs FROM UserGameSystem ugs WHERE ugs.gameSystem.id = ?1 AND ugs.yearsExperience >= ?2 AND ugs.isActive = true")
    List<UserGameSystem> findByGameSystemIdAndYearsExperienceGreaterThanEqual(Long gameSystemId, Integer minYears);

    // Check if user plays a specific game system
    boolean existsByUserIdAndGameSystemId(Long userId, Long gameSystemId);

    // Count active players for a game system
    @Query("SELECT COUNT(ugs) FROM UserGameSystem ugs WHERE ugs.gameSystem.id = ?1 AND ugs.isActive = true")
    Long countActivePlayersByGameSystemId(Long gameSystemId);

    // Get average skill rating for a game system
    @Query("SELECT AVG(ugs.skillRating) FROM UserGameSystem ugs WHERE ugs.gameSystem.id = ?1 AND ugs.isActive = true AND ugs.skillRating IS NOT NULL")
    Double getAverageSkillRatingByGameSystemId(Long gameSystemId);
}