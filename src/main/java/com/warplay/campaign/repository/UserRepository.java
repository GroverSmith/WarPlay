package com.warplay.campaign.repository;

import com.warplay.campaign.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}