package com.warplay.repository;

import com.warplay.entity.ArmyUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArmyUnitRepository extends JpaRepository<ArmyUnit, Long> {
    
    /**
     * Find all units for a specific army
     */
    @Query("SELECT au FROM ArmyUnit au WHERE au.armyId = :armyId AND au.deletedTimestamp IS NULL")
    List<ArmyUnit> findByArmyId(@Param("armyId") Long armyId);
    
    /**
     * Find all armies that contain a specific unit
     */
    @Query("SELECT au FROM ArmyUnit au WHERE au.unitId = :unitId AND au.deletedTimestamp IS NULL")
    List<ArmyUnit> findByUnitId(@Param("unitId") Long unitId);
    
    /**
     * Check if unit is already in army
     */
    @Query("SELECT COUNT(au) > 0 FROM ArmyUnit au WHERE au.armyId = :armyId AND au.unitId = :unitId AND au.deletedTimestamp IS NULL")
    boolean existsByArmyIdAndUnitId(@Param("armyId") Long armyId, @Param("unitId") Long unitId);
    
    /**
     * Delete all units from an army (soft delete)
     */
    @Query("UPDATE ArmyUnit au SET au.deletedTimestamp = CURRENT_TIMESTAMP WHERE au.armyId = :armyId AND au.deletedTimestamp IS NULL")
    void deleteByArmyId(@Param("armyId") Long armyId);
}
