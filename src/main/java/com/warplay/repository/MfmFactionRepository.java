package com.warplay.repository;

import com.warplay.entity.MfmFaction;
import com.warplay.entity.MfmVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmFactionRepository extends JpaRepository<MfmFaction, Long> {
    
    /**
     * Find factions by MFM version
     */
    List<MfmFaction> findByMfmVersion(MfmVersion mfmVersion);
    
    /**
     * Find factions by version string
     */
    @Query("SELECT f FROM MfmFaction f WHERE f.mfmVersion.version = :version ORDER BY f.name ASC")
    List<MfmFaction> findByMfmVersionVersion(@Param("version") String version);
    
    /**
     * Find faction by name and version
     */
    @Query("SELECT f FROM MfmFaction f WHERE f.name = :name AND f.mfmVersion.version = :version")
    Optional<MfmFaction> findByNameAndMfmVersionVersion(@Param("name") String name, @Param("version") String version);
    
    /**
     * Find faction by name in latest version
     */
    @Query("SELECT f FROM MfmFaction f WHERE f.name = :name AND f.mfmVersion.isLatest = true")
    Optional<MfmFaction> findByNameInLatestVersion(@Param("name") String name);
    
    /**
     * Get all faction names for a version
     */
    @Query("SELECT f.name FROM MfmFaction f WHERE f.mfmVersion.version = :version ORDER BY f.name ASC")
    List<String> findFactionNamesByVersion(@Param("version") String version);
    
    /**
     * Get all faction names in latest version
     */
    @Query("SELECT f.name FROM MfmFaction f WHERE f.mfmVersion.isLatest = true ORDER BY f.name ASC")
    List<String> findFactionNamesInLatestVersion();
    
    /**
     * Check if faction exists in version
     */
    @Query("SELECT COUNT(f) > 0 FROM MfmFaction f WHERE f.name = :name AND f.mfmVersion.version = :version")
    boolean existsByNameAndMfmVersionVersion(@Param("name") String name, @Param("version") String version);
}
