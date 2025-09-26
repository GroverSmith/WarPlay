package com.warplay.campaign.repository;

import com.warplay.campaign.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by Google ID (for OAuth login)
    Optional<User> findByGoogleId(String googleId);

    // Find by email
    Optional<User> findByEmail(String email);

    // Find all non-deleted users
    @Query("SELECT u FROM User u WHERE u.deletedTimestamp IS NULL")
    List<User> findAllActive();

    // Find by ID excluding deleted users
    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.deletedTimestamp IS NULL")
    Optional<User> findByIdActive(Long id);

    // Check if email exists (excluding deleted users)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = ?1 AND u.deletedTimestamp IS NULL")
    boolean existsByEmailActive(String email);

    // Find by Discord handle
    Optional<User> findByDiscordHandle(String discordHandle);

    // Find users who play a specific game system
    @Query("SELECT DISTINCT u FROM User u JOIN u.userGameSystems ugs WHERE ugs.gameSystem.id = :gameSystemId AND ugs.isActive = true AND u.deletedTimestamp IS NULL")
    List<User> findUsersByGameSystemId(@Param("gameSystemId") Long gameSystemId);

    // Find users who play a specific game system with minimum skill rating
    @Query("SELECT DISTINCT u FROM User u JOIN u.userGameSystems ugs WHERE ugs.gameSystem.id = :gameSystemId AND ugs.skillRating >= :minRating AND ugs.isActive = true AND u.deletedTimestamp IS NULL")
    List<User> findUsersByGameSystemIdAndMinSkillRating(@Param("gameSystemId") Long gameSystemId, @Param("minRating") Integer minRating);
}