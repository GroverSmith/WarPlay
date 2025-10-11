package com.warplay.repository;

import com.warplay.entity.Force;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForceRepository extends JpaRepository<Force, Long> {
    
    // Find all non-deleted forces for a club
    @Query("SELECT f FROM Force f WHERE f.clubId = :clubId AND f.deletedTimestamp IS NULL ORDER BY f.createdTimestamp DESC")
    List<Force> findByClubIdAndDeletedTimestampIsNull(@Param("clubId") Long clubId);
    
    // Find all non-deleted forces for a crusade
    @Query("SELECT f FROM Force f WHERE f.crusadeId = :crusadeId AND f.deletedTimestamp IS NULL ORDER BY f.createdTimestamp DESC")
    List<Force> findByCrusadeIdAndDeletedTimestampIsNull(@Param("crusadeId") Long crusadeId);
    
    // Find all non-deleted forces for a user
    @Query("SELECT f FROM Force f WHERE f.userId = :userId AND f.deletedTimestamp IS NULL ORDER BY f.createdTimestamp DESC")
    List<Force> findByUserIdAndDeletedTimestampIsNull(@Param("userId") Long userId);
    
    // Find a specific non-deleted force by ID
    @Query("SELECT f FROM Force f WHERE f.id = :id AND f.deletedTimestamp IS NULL")
    Optional<Force> findByIdAndDeletedTimestampIsNull(@Param("id") Long id);
    
    // Find all non-deleted forces
    @Query("SELECT f FROM Force f WHERE f.deletedTimestamp IS NULL ORDER BY f.createdTimestamp DESC")
    List<Force> findAllNonDeleted();
}

