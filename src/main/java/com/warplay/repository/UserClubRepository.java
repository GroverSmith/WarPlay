package com.warplay.repository;

import com.warplay.entity.UserClub;
import com.warplay.entity.UserClub.ClubRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserClubRepository extends JpaRepository<UserClub, Long> {

    // Find all active memberships
    List<UserClub> findByIsActiveTrue();

    // Find all memberships for a user
    List<UserClub> findByUserId(Long userId);

    // Find active memberships for a user
    List<UserClub> findByUserIdAndIsActiveTrue(Long userId);
    
    // Find active memberships for a user with eager loading of Club
    @Query("SELECT uc FROM UserClub uc JOIN FETCH uc.club WHERE uc.user.id = :userId AND uc.isActive = true")
    List<UserClub> findByUserIdAndIsActiveTrueWithClub(@Param("userId") Long userId);

    // Find all memberships for a club
    List<UserClub> findByClubId(Long clubId);

    // Find active memberships for a club
    List<UserClub> findByClubIdAndIsActiveTrue(Long clubId);
    
    // Find active memberships for a club with eager loading of User and Club
    @Query("SELECT uc FROM UserClub uc JOIN FETCH uc.user JOIN FETCH uc.club WHERE uc.club.id = :clubId AND uc.isActive = true")
    List<UserClub> findByClubIdAndIsActiveTrueWithUser(@Param("clubId") Long clubId);

    // Find specific user-club relationship
    Optional<UserClub> findByUserIdAndClubId(Long userId, Long clubId);

    // Find active user-club relationship
    Optional<UserClub> findByUserIdAndClubIdAndIsActiveTrue(Long userId, Long clubId);

    // Find memberships by role
    List<UserClub> findByClubIdAndRole(Long clubId, ClubRole role);

    // Find active memberships by role
    List<UserClub> findByClubIdAndRoleAndIsActiveTrue(Long clubId, ClubRole role);

    // Find clubs by user and game system
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.club.gameSystem = :gameSystem " +
            "AND uc.isActive = true")
    List<UserClub> findActiveClubsByUserAndGameSystem(
            @Param("userId") Long userId,
            @Param("gameSystem") String gameSystem);

    // Find users in clubs by location
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.club.countryCode = :countryCode " +
            "AND (:provinceCode IS NULL OR uc.club.provinceCode = :provinceCode) " +
            "AND (:city IS NULL OR uc.club.city = :city) " +
            "AND uc.isActive = true")
    List<UserClub> findActiveUsersByLocation(
            @Param("countryCode") String countryCode,
            @Param("provinceCode") String provinceCode,
            @Param("city") String city);

    // Check if user is member of club
    @Query("SELECT COUNT(uc) > 0 FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.club.id = :clubId " +
            "AND uc.isActive = true")
    boolean isUserMemberOfClub(@Param("userId") Long userId, @Param("clubId") Long clubId);

    // Check if user has admin rights in club
    @Query("SELECT COUNT(uc) > 0 FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.club.id = :clubId " +
            "AND uc.role IN ('ADMIN', 'OWNER') " +
            "AND uc.isActive = true")
    boolean isUserAdminOfClub(@Param("userId") Long userId, @Param("clubId") Long clubId);

    // Check if user is owner of club
    @Query("SELECT COUNT(uc) > 0 FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.club.id = :clubId " +
            "AND uc.role = 'OWNER' " +
            "AND uc.isActive = true")
    boolean isUserOwnerOfClub(@Param("userId") Long userId, @Param("clubId") Long clubId);

    // Count active members in club
    @Query(value = "SELECT COUNT(*) FROM user_clubs WHERE club_id = :clubId AND is_active = true", nativeQuery = true)
    Long countActiveMembersByClubId(@Param("clubId") Long clubId);

    // Count active members by role in club
    @Query("SELECT COUNT(uc) FROM UserClub uc " +
            "WHERE uc.club.id = :clubId " +
            "AND uc.role = :role " +
            "AND uc.isActive = true")
    Long countActiveMembersByClubIdAndRole(@Param("clubId") Long clubId, @Param("role") ClubRole role);

    // Get club statistics
    @Query("SELECT uc.club.gameSystem, COUNT(DISTINCT uc.club.id), COUNT(DISTINCT uc.user.id) " +
            "FROM UserClub uc " +
            "WHERE uc.isActive = true " +
            "GROUP BY uc.club.gameSystem")
    List<Object[]> getClubStatsByGameSystem();

    // Find clubs user owns
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.role = 'OWNER' " +
            "AND uc.isActive = true")
    List<UserClub> findClubsOwnedByUser(@Param("userId") Long userId);

    // Find clubs user administers (admin or owner)
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND uc.role IN ('ADMIN', 'OWNER') " +
            "AND uc.isActive = true")
    List<UserClub> findClubsAdministeredByUser(@Param("userId") Long userId);

    // Search clubs by user and club name
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.user.id = :userId " +
            "AND LOWER(uc.club.name) LIKE LOWER(CONCAT('%', :clubName, '%')) " +
            "AND uc.isActive = true")
    List<UserClub> findUserClubsByClubName(
            @Param("userId") Long userId,
            @Param("clubName") String clubName);

    // Find recent club joins
    @Query("SELECT uc FROM UserClub uc " +
            "WHERE uc.club.id = :clubId " +
            "AND uc.isActive = true " +
            "ORDER BY uc.joinedTimestamp DESC")
    List<UserClub> findRecentJoinsByClub(@Param("clubId") Long clubId);

    // Check if club has any active members (for deletion validation)
    @Query("SELECT COUNT(uc) FROM UserClub uc " +
            "WHERE uc.club.id = :clubId " +
            "AND uc.isActive = true")
    Long countActiveMembersInClub(@Param("clubId") Long clubId);
}