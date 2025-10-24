package com.warplay.repository;

import com.warplay.entity.MfmDetachment;
import com.warplay.entity.MfmEnhancement;
import com.warplay.entity.MfmFaction;
import com.warplay.entity.MfmVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmEnhancementRepository extends JpaRepository<MfmEnhancement, Long> {
    
    /**
     * Find enhancements by detachment
     */
    List<MfmEnhancement> findByDetachment(MfmDetachment detachment);
    
    /**
     * Find enhancements by detachment name, faction, and version
     */
    @Query("SELECT e FROM MfmEnhancement e JOIN FETCH e.detachment d JOIN FETCH d.faction f JOIN FETCH f.mfmVersion WHERE d.name = :detachmentName AND f.name = :factionName AND f.mfmVersion.version = :version ORDER BY e.name ASC")
    List<MfmEnhancement> findByDetachmentNameAndFactionAndVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find enhancements by detachment name and faction in latest version
     */
    @Query("SELECT e FROM MfmEnhancement e JOIN FETCH e.detachment d JOIN FETCH d.faction f JOIN FETCH f.mfmVersion WHERE d.name = :detachmentName AND f.name = :factionName AND f.mfmVersion.isLatest = true ORDER BY e.name ASC")
    List<MfmEnhancement> findByDetachmentNameAndFactionInLatestVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName);
    
    /**
     * Find enhancement by name, detachment, faction, and version
     */
    @Query("SELECT e FROM MfmEnhancement e JOIN FETCH e.detachment d JOIN FETCH d.faction f JOIN FETCH f.mfmVersion WHERE e.name = :enhancementName AND d.name = :detachmentName AND f.name = :factionName AND f.mfmVersion.version = :version")
    Optional<MfmEnhancement> findByNameAndDetachmentAndFactionAndVersion(@Param("enhancementName") String enhancementName, @Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Find enhancement by name, detachment, and faction in latest version
     */
    @Query("SELECT e FROM MfmEnhancement e JOIN FETCH e.detachment d JOIN FETCH d.faction f JOIN FETCH f.mfmVersion WHERE e.name = :enhancementName AND d.name = :detachmentName AND f.name = :factionName AND f.mfmVersion.isLatest = true")
    Optional<MfmEnhancement> findByNameAndDetachmentAndFactionInLatestVersion(@Param("enhancementName") String enhancementName, @Param("detachmentName") String detachmentName, @Param("factionName") String factionName);
    
    /**
     * Get all enhancement names for a detachment, faction, and version
     */
    @Query("SELECT e.name FROM MfmEnhancement e WHERE e.detachment.name = :detachmentName AND e.detachment.faction.name = :factionName AND e.detachment.faction.mfmVersion.version = :version ORDER BY e.name ASC")
    List<String> findEnhancementNamesByDetachmentAndFactionAndVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Get all enhancement names for a detachment and faction in latest version
     */
    @Query("SELECT e.name FROM MfmEnhancement e WHERE e.detachment.name = :detachmentName AND e.detachment.faction.name = :factionName AND e.detachment.faction.mfmVersion.isLatest = true ORDER BY e.name ASC")
    List<String> findEnhancementNamesByDetachmentAndFactionInLatestVersion(@Param("detachmentName") String detachmentName, @Param("factionName") String factionName);
    
    /**
     * Get points for a specific enhancement
     */
    @Query("SELECT e.points FROM MfmEnhancement e WHERE e.name = :enhancementName AND e.detachment.name = :detachmentName AND e.detachment.faction.name = :factionName AND e.detachment.faction.mfmVersion.version = :version")
    Optional<Integer> findPointsByNameAndDetachmentAndFactionAndVersion(@Param("enhancementName") String enhancementName, @Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Get points for a specific enhancement in latest version
     */
    @Query("SELECT e.points FROM MfmEnhancement e WHERE e.name = :enhancementName AND e.detachment.name = :detachmentName AND e.detachment.faction.name = :factionName AND e.detachment.faction.mfmVersion.isLatest = true")
    Optional<Integer> findPointsByNameAndDetachmentAndFactionInLatestVersion(@Param("enhancementName") String enhancementName, @Param("detachmentName") String detachmentName, @Param("factionName") String factionName);
    
    /**
     * Check if enhancement exists in detachment, faction, and version
     */
    @Query("SELECT COUNT(e) > 0 FROM MfmEnhancement e WHERE e.name = :enhancementName AND e.detachment.name = :detachmentName AND e.detachment.faction.name = :factionName AND e.detachment.faction.mfmVersion.version = :version")
    boolean existsByNameAndDetachmentAndFactionAndVersion(@Param("enhancementName") String enhancementName, @Param("detachmentName") String detachmentName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Delete enhancements by MFM version
     */
    @Modifying
    @Query("DELETE FROM MfmEnhancement e WHERE e.detachment.faction.mfmVersion = :mfmVersion")
    void deleteByDetachmentFactionMfmVersion(@Param("mfmVersion") MfmVersion mfmVersion);
    
    /**
     * Count enhancements by faction
     */
    long countByDetachmentFaction(MfmFaction faction);
}
