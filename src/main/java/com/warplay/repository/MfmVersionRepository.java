package com.warplay.repository;

import com.warplay.entity.MfmVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmVersionRepository extends JpaRepository<MfmVersion, Long> {
    
    /**
     * Find MFM version by version string
     */
    Optional<MfmVersion> findByVersion(String version);
    
    /**
     * Find the latest MFM version
     */
    Optional<MfmVersion> findByIsLatestTrue();
    
    /**
     * Find the latest active MFM version
     */
    Optional<MfmVersion> findByIsLatestTrueAndIsActiveTrue();
    
    /**
     * Find all versions ordered by version string
     */
    List<MfmVersion> findAllByOrderByVersionAsc();
    
    /**
     * Find all active versions ordered by version string
     */
    List<MfmVersion> findByIsActiveTrueOrderByVersionAsc();
    
    /**
     * Check if a version exists
     */
    boolean existsByVersion(String version);
    
    /**
     * Get all version strings
     */
    @Query("SELECT v.version FROM MfmVersion v ORDER BY v.version ASC")
    List<String> findAllVersionStrings();
    
    /**
     * Get all active version strings
     */
    @Query("SELECT v.version FROM MfmVersion v WHERE v.isActive = true ORDER BY v.version ASC")
    List<String> findActiveVersionStrings();
    
    /**
     * Find the highest version string
     */
    @Query("SELECT v.version FROM MfmVersion v ORDER BY v.version DESC LIMIT 1")
    Optional<String> findHighestVersion();
    
    /**
     * Find the highest active version string
     */
    @Query("SELECT v.version FROM MfmVersion v WHERE v.isActive = true ORDER BY v.version DESC LIMIT 1")
    Optional<String> findHighestActiveVersion();
}
