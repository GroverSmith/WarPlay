package com.warplay.repository;

import com.warplay.entity.Army;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArmyRepository extends JpaRepository<Army, Long> {
    
    /**
     * Find all armies for a specific force
     */
    @Query("SELECT a FROM Army a WHERE a.forceId = :forceId AND a.deletedTimestamp IS NULL ORDER BY a.createdTimestamp DESC")
    List<Army> findByForceId(@Param("forceId") Long forceId);
    
    /**
     * Find all armies for a specific user
     */
    @Query("SELECT a FROM Army a WHERE a.userId = :userId AND a.deletedTimestamp IS NULL ORDER BY a.createdTimestamp DESC")
    List<Army> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find army by ID and user ID (for authorization)
     */
    @Query("SELECT a FROM Army a WHERE a.id = :id AND a.userId = :userId AND a.deletedTimestamp IS NULL")
    Optional<Army> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * Find army by ID and force ID (for authorization)
     */
    @Query("SELECT a FROM Army a WHERE a.id = :id AND a.forceId = :forceId AND a.deletedTimestamp IS NULL")
    Optional<Army> findByIdAndForceId(@Param("id") Long id, @Param("forceId") Long forceId);
    
    /**
     * Check if army exists and belongs to user
     */
    @Query("SELECT COUNT(a) > 0 FROM Army a WHERE a.id = :id AND a.userId = :userId AND a.deletedTimestamp IS NULL")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * Count armies for a force
     */
    @Query("SELECT COUNT(a) FROM Army a WHERE a.forceId = :forceId AND a.deletedTimestamp IS NULL")
    long countByForceId(@Param("forceId") Long forceId);
}
