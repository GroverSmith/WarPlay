package com.warplay.repository;

import com.warplay.entity.Crusade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrusadeRepository extends JpaRepository<Crusade, Long> {

    // Find all active (non-deleted) crusades
    List<Crusade> findByDeletedTimestampIsNull();

    // Find active crusade by ID
    Optional<Crusade> findByIdAndDeletedTimestampIsNull(Long id);

    // Find crusades by club
    List<Crusade> findByClubIdAndDeletedTimestampIsNull(Long clubId);

    // Find crusades by club and state
    List<Crusade> findByClubIdAndStateAndDeletedTimestampIsNull(Long clubId, String state);

    // Find active crusades (state = 'Active')
    @Query("SELECT c FROM Crusade c WHERE c.state = 'Active' AND c.deletedTimestamp IS NULL")
    List<Crusade> findActiveCrusades();

    // Find active crusades for a specific club
    @Query("SELECT c FROM Crusade c WHERE c.clubId = :clubId AND c.state = 'Active' AND c.deletedTimestamp IS NULL")
    List<Crusade> findActiveCrusadesByClub(@Param("clubId") Long clubId);

    // Search crusades by name
    List<Crusade> findByNameContainingIgnoreCaseAndDeletedTimestampIsNull(String name);

    // Check if crusade name exists for a specific club
    @Query("SELECT COUNT(c) > 0 FROM Crusade c WHERE c.name = :name " +
            "AND c.clubId = :clubId " +
            "AND c.deletedTimestamp IS NULL " +
            "AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean existsByNameAndClubExcludingId(
            @Param("name") String name,
            @Param("clubId") Long clubId,
            @Param("excludeId") Long excludeId);

    // Count crusades by club
    @Query("SELECT COUNT(c) FROM Crusade c WHERE c.clubId = :clubId AND c.deletedTimestamp IS NULL")
    long countByClub(@Param("clubId") Long clubId);

    // Count active crusades by club
    @Query("SELECT COUNT(c) FROM Crusade c WHERE c.clubId = :clubId AND c.state = 'Active' AND c.deletedTimestamp IS NULL")
    long countActiveCrusadesByClub(@Param("clubId") Long clubId);
}
