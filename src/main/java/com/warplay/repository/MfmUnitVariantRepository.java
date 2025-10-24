package com.warplay.repository;

import com.warplay.entity.MfmUnit;
import com.warplay.entity.MfmUnitVariant;
import com.warplay.entity.MfmVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MfmUnitVariantRepository extends JpaRepository<MfmUnitVariant, Long> {
    
    /**
     * Find variants by unit
     */
    List<MfmUnitVariant> findByUnit(MfmUnit unit);
    
    /**
     * Find variant by unit and model count
     */
    Optional<MfmUnitVariant> findByUnitAndModelCount(MfmUnit unit, Integer modelCount);
    
    /**
     * Find variant by unit name, faction, version, and model count
     */
    @Query("SELECT v FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.version = :version AND v.modelCount = :modelCount")
    Optional<MfmUnitVariant> findByUnitNameAndFactionAndVersionAndModelCount(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("version") String version, @Param("modelCount") Integer modelCount);
    
    /**
     * Find variant by unit name, faction, version, and model count in latest version
     */
    @Query("SELECT v FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.isLatest = true AND v.modelCount = :modelCount")
    Optional<MfmUnitVariant> findByUnitNameAndFactionInLatestVersionAndModelCount(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("modelCount") Integer modelCount);
    
    /**
     * Get all model counts for a unit
     */
    @Query("SELECT v.modelCount FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.version = :version ORDER BY v.modelCount ASC")
    List<Integer> findModelCountsByUnitNameAndFactionAndVersion(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("version") String version);
    
    /**
     * Get all model counts for a unit in latest version
     */
    @Query("SELECT v.modelCount FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.isLatest = true ORDER BY v.modelCount ASC")
    List<Integer> findModelCountsByUnitNameAndFactionInLatestVersion(@Param("unitName") String unitName, @Param("factionName") String factionName);
    
    /**
     * Get points for a specific unit variant
     */
    @Query("SELECT v.points FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.version = :version AND v.modelCount = :modelCount")
    Optional<Integer> findPointsByUnitNameAndFactionAndVersionAndModelCount(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("version") String version, @Param("modelCount") Integer modelCount);
    
    /**
     * Get points for a specific unit variant in latest version
     */
    @Query("SELECT v.points FROM MfmUnitVariant v WHERE v.unit.name = :unitName AND v.unit.faction.name = :factionName AND v.unit.faction.mfmVersion.isLatest = true AND v.modelCount = :modelCount")
    Optional<Integer> findPointsByUnitNameAndFactionInLatestVersionAndModelCount(@Param("unitName") String unitName, @Param("factionName") String factionName, @Param("modelCount") Integer modelCount);
    
    /**
     * Delete unit variants by MFM version
     */
    @Modifying
    @Query("DELETE FROM MfmUnitVariant v WHERE v.unit.faction.mfmVersion = :mfmVersion")
    void deleteByUnitFactionMfmVersion(@Param("mfmVersion") MfmVersion mfmVersion);
}
