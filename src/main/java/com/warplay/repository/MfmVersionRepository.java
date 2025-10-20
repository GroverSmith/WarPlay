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
     * Find all versions ordered by version string
     */
    List<MfmVersion> findAllByOrderByVersionAsc();
    
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
     * Find the highest version string
     */
    @Query("SELECT v.version FROM MfmVersion v ORDER BY v.version DESC LIMIT 1")
    Optional<String> findHighestVersion();
}
