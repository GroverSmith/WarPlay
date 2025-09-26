package com.warplay.campaign.repository;

import com.warplay.campaign.entity.GameSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSystemRepository extends JpaRepository<GameSystem, Long> {

    // Find by name
    Optional<GameSystem> findByName(String name);

    // Find by short name
    Optional<GameSystem> findByShortName(String shortName);

    // Check if name exists
    boolean existsByName(String name);

    // Find by publisher
    List<GameSystem> findByPublisher(String publisher);

    // Find all ordered by name
    @Query("SELECT gs FROM GameSystem gs ORDER BY gs.name")
    List<GameSystem> findAllOrderedByName();

    // Search by name (case insensitive)
    @Query("SELECT gs FROM GameSystem gs WHERE LOWER(gs.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<GameSystem> searchByNameContaining(String searchTerm);
}