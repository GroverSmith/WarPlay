package com.warplay.repository;

import com.warplay.entity.MfmDetachment;
import com.warplay.entity.MfmFaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmDetachmentRepository extends JpaRepository<MfmDetachment, Long> {
    
    /**
     * Find detachments by faction
     */
    List<MfmDetachment> findByFaction(MfmFaction faction);
    
    /**
     * Find detachments by faction name and version
     */
    @Query("SELECT d FROM MfmDetachment d WHERE d.faction.name = :factionName AND d.faction.mfmVersion.version = :version ORDER BY d.name ASC")
    List<MfmDetachment> findByFactionNameAndVersion(@Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find detachments by faction name in latest version
     */
    @Query("SELECT d FROM MfmDetachment d WHERE d.faction.name = :factionName AND d.faction.mfmVersion.isLatest = true ORDER BY d.name ASC")
    List<MfmDetachment> findByFactionNameInLatestVersion(@Param("factionName") String factionName);
    
    /**
     * Find detachment by name, faction, and version
     */
    @Query("SELECT d FROM MfmDetachment d WHERE d.name = :detachmentName AND d.faction.name = :factionName AND d.faction.mfmVersion.version = :version")
    Optional<MfmDetachment> findByNameAndFactionAndVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find detachment by name and faction in latest version
     */
    @Query("SELECT d FROM MfmDetachment d WHERE d.name = :detachmentName AND d.faction.name = :factionName AND d.faction.mfmVersion.isLatest = true")
    Optional<MfmDetachment> findByNameAndFactionInLatestVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName);
    
    /**
     * Get all detachment names for a faction and version
     */
    @Query("SELECT d.name FROM MfmDetachment d WHERE d.faction.name = :factionName AND d.faction.mfmVersion.version = :version ORDER BY d.name ASC")
    List<String> findDetachmentNamesByFactionAndVersion(@Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Get all detachment names for a faction in latest version
     */
    @Query("SELECT d.name FROM MfmDetachment d WHERE d.faction.name = :factionName AND d.faction.mfmVersion.isLatest = true ORDER BY d.name ASC")
    List<String> findDetachmentNamesByFactionInLatestVersion(@Param("factionName") String factionName);
    
    /**
     * Check if detachment exists in faction and version
     */
    @Query("SELECT COUNT(d) > 0 FROM MfmDetachment d WHERE d.name = :detachmentName AND d.faction.name = :factionName AND d.faction.mfmVersion.version = :version")
    boolean existsByNameAndFactionAndVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
}
