package com.warplay.repository;

import com.warplay.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    // Find all active (non-deleted) clubs
    List<Club> findByDeletedTimestampIsNull();

    // Find active club by ID
    Optional<Club> findByIdAndDeletedTimestampIsNull(Long id);

    // Find clubs by owner
    List<Club> findByOwnerIdAndDeletedTimestampIsNull(Long ownerId);

    // Find clubs by game system
    List<Club> findByGameSystemAndDeletedTimestampIsNull(String gameSystem);

    // Find clubs by location
    List<Club> findByCountryCodeAndDeletedTimestampIsNull(String countryCode);

    List<Club> findByCountryCodeAndProvinceCodeAndDeletedTimestampIsNull(
            String countryCode, String provinceCode);

    List<Club> findByCountryCodeAndProvinceCodeAndCityAndDeletedTimestampIsNull(
            String countryCode, String provinceCode, String city);

    // Search clubs by name (case-insensitive)
    List<Club> findByNameContainingIgnoreCaseAndDeletedTimestampIsNull(String name);

    // Custom query to find clubs by game system and location
    @Query("SELECT c FROM Club c WHERE c.gameSystem = :gameSystem " +
            "AND c.countryCode = :countryCode " +
            "AND (:provinceCode IS NULL OR c.provinceCode = :provinceCode) " +
            "AND c.deletedTimestamp IS NULL")
    List<Club> findByGameSystemAndLocation(
            @Param("gameSystem") String gameSystem,
            @Param("countryCode") String countryCode,
            @Param("provinceCode") String provinceCode);

    // Check if club name exists for a specific game system (for validation)
    @Query("SELECT COUNT(c) > 0 FROM Club c WHERE c.name = :name " +
            "AND c.gameSystem = :gameSystem " +
            "AND c.deletedTimestamp IS NULL " +
            "AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean existsByNameAndGameSystemExcludingId(
            @Param("name") String name,
            @Param("gameSystem") String gameSystem,
            @Param("excludeId") Long excludeId);

    // Get clubs count by game system
    @Query("SELECT c.gameSystem, COUNT(c) FROM Club c " +
            "WHERE c.deletedTimestamp IS NULL " +
            "GROUP BY c.gameSystem")
    List<Object[]> countClubsByGameSystem();

    // Get clubs count by country
    @Query("SELECT c.countryCode, COUNT(c) FROM Club c " +
            "WHERE c.deletedTimestamp IS NULL " +
            "GROUP BY c.countryCode")
    List<Object[]> countClubsByCountry();
}