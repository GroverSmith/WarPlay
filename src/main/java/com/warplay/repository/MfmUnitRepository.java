package com.warplay.repository;

import com.warplay.entity.MfmFaction;
import com.warplay.entity.MfmUnit;
import com.warplay.entity.MfmVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmUnitRepository extends JpaRepository<MfmUnit, Long> {
    
    /**
     * Find units by faction
     */
    List<MfmUnit> findByFaction(MfmFaction faction);
    
    /**
     * Find unit by name and faction
     */
    Optional<MfmUnit> findByNameAndFaction(String name, MfmFaction faction);
    
    /**
     * Find units by faction name and version
     */
    @Query("SELECT u FROM MfmUnit u WHERE u.faction.name = :factionName AND u.faction.mfmVersion.version = :version ORDER BY u.name ASC")
    List<MfmUnit> findByFactionNameAndVersion(@Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find units by faction name in latest version
     */
    @Query("SELECT u FROM MfmUnit u WHERE u.faction.name = :factionName AND u.faction.mfmVersion.isLatest = true ORDER BY u.name ASC")
    List<MfmUnit> findByFactionNameInLatestVersion(@Param("factionName") String factionName);
    
    /**
     * Find unit by name, faction, and version
     */
    @Query("SELECT u FROM MfmUnit u WHERE u.name = :unitName AND u.faction.name = :factionName AND u.faction.mfmVersion.version = :version")
    Optional<MfmUnit> findByNameAndFactionAndVersion(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find unit by name and faction in latest version
     */
    @Query("SELECT u FROM MfmUnit u WHERE u.name = :unitName AND u.faction.name = :factionName AND u.faction.mfmVersion.isLatest = true")
    Optional<MfmUnit> findByNameAndFactionInLatestVersion(@Param("unitName") String unitName, @Param("factionName") String factionName);
    
    /**
     * Get all unit names for a faction and version
     */
    @Query("SELECT u.name FROM MfmUnit u WHERE u.faction.name = :factionName AND u.faction.mfmVersion.version = :version ORDER BY u.name ASC")
    List<String> findUnitNamesByFactionAndVersion(@Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Get all unit names for a faction in latest version
     */
    @Query("SELECT u.name FROM MfmUnit u WHERE u.faction.name = :factionName AND u.faction.mfmVersion.isLatest = true ORDER BY u.name ASC")
    List<String> findUnitNamesByFactionInLatestVersion(@Param("factionName") String factionName);
    
    /**
     * Check if unit exists in faction and version
     */
    @Query("SELECT COUNT(u) > 0 FROM MfmUnit u WHERE u.name = :unitName AND u.faction.name = :factionName AND u.faction.mfmVersion.version = :version")
    boolean existsByNameAndFactionAndVersion(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Delete units by MFM version
     */
    @Modifying
    @Query("DELETE FROM MfmUnit u WHERE u.faction.mfmVersion = :mfmVersion")
    void deleteByFactionMfmVersion(@Param("mfmVersion") MfmVersion mfmVersion);
    
    /**
     * Count units by faction
     */
    long countByFaction(MfmFaction faction);
}
