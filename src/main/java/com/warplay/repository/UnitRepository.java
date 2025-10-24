package com.warplay.repository;

import com.warplay.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    
    // Find all non-deleted units for a force
    @Query("SELECT u FROM Unit u WHERE u.forceId = :forceId AND u.deletedTimestamp IS NULL ORDER BY u.createdTimestamp DESC")
    List<Unit> findByForceIdAndDeletedTimestampIsNull(@Param("forceId") Long forceId);
    
    // Find all non-deleted units for a user
    @Query("SELECT u FROM Unit u WHERE u.userId = :userId AND u.deletedTimestamp IS NULL ORDER BY u.createdTimestamp DESC")
    List<Unit> findByUserIdAndDeletedTimestampIsNull(@Param("userId") Long userId);
    
    // Find a specific non-deleted unit by ID
    @Query("SELECT u FROM Unit u WHERE u.id = :id AND u.deletedTimestamp IS NULL")
    Optional<Unit> findByIdAndDeletedTimestampIsNull(@Param("id") Long id);
    
    // Find all non-deleted units
    @Query("SELECT u FROM Unit u WHERE u.deletedTimestamp IS NULL ORDER BY u.createdTimestamp DESC")
    List<Unit> findAllNonDeleted();
    
    // Find units by force ID and user ID (for authorization)
    @Query("SELECT u FROM Unit u WHERE u.forceId = :forceId AND u.userId = :userId AND u.deletedTimestamp IS NULL")
    List<Unit> findByForceIdAndUserIdAndDeletedTimestampIsNull(@Param("forceId") Long forceId, @Param("userId") Long userId);
}
