package com.warplay.service;

import com.warplay.dto.ClubUpdateDTO;
import com.warplay.dto.ClubWithMemberCount;
import com.warplay.entity.Club;
import com.warplay.repository.ClubRepository;
import com.warplay.repository.UserClubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClubService {

    private static final Logger logger = LoggerFactory.getLogger(ClubService.class);

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserClubRepository userClubRepository;


    @Autowired
    private LoggingService loggingService;

    public List<Club> getAllActiveClubs() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active clubs from database");

            List<Club> clubs = clubRepository.findByDeletedTimestampIsNull();

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("clubs", "SELECT_ALL_ACTIVE", true,
                "Retrieved " + clubs.size() + " active clubs");

            logger.info("Successfully retrieved {} active clubs from database", clubs.size());
            return clubs;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("clubs", "SELECT_ALL_ACTIVE", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve active clubs from database", e);
            throw new RuntimeException("Failed to retrieve clubs", e);
        }
    }

    public List<ClubWithMemberCount> getAllActiveClubsWithMemberCount() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active clubs with member count from database");

            List<Club> clubs = clubRepository.findByDeletedTimestampIsNull();
            
            List<ClubWithMemberCount> clubsWithMemberCount = clubs.stream()
                .map(club -> {
                    Long memberCount = userClubRepository.countActiveMembersByClubId(club.getId());
                    return new ClubWithMemberCount(club, memberCount);
                })
                .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("clubs", "SELECT_ALL_ACTIVE_WITH_MEMBER_COUNT", true,
                "Retrieved " + clubsWithMemberCount.size() + " active clubs with member counts");

            logger.info("Successfully retrieved {} active clubs with member counts from database", clubsWithMemberCount.size());
            return clubsWithMemberCount;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("clubs", "SELECT_ALL_ACTIVE_WITH_MEMBER_COUNT", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve active clubs with member count from database", e);
            throw new RuntimeException("Failed to retrieve clubs with member count", e);
        }
    }

    public Optional<Club> getActiveClubById(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active club by ID: {}", id);

            Optional<Club> club = clubRepository.findByIdAndDeletedTimestampIsNull(id);

            long duration = System.currentTimeMillis() - startTime;

            if (club.isPresent()) {
                logger.info("Successfully retrieved club: {} (ID: {})",
                        club.get().getName(), id);
            } else {
                logger.warn("Active club not found: {}", id);
            }

            return club;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to retrieve club by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve club", e);
        }
    }

    public Optional<ClubWithMemberCount> getActiveClubByIdWithMemberCount(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active club by ID with member count: {}", id);

            Optional<Club> club = clubRepository.findByIdAndDeletedTimestampIsNull(id);

            if (club.isPresent()) {
                Long memberCount = userClubRepository.countActiveMembersByClubId(id);
                ClubWithMemberCount clubWithMemberCount = new ClubWithMemberCount(club.get(), memberCount);
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("Successfully retrieved club with member count: {} (ID: {}, Members: {})",
                        club.get().getName(), id, memberCount);
                
                return Optional.of(clubWithMemberCount);
            } else {
                logger.warn("Active club not found: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to retrieve club by ID with member count: {}", id, e);
            throw new RuntimeException("Failed to retrieve club with member count", e);
        }
    }

    public Club createClub(Club club) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating new club: {}", club.getName());

            // Validate club
            validateClub(club, null);

            // Check for duplicate name in the same game system
            if (clubRepository.existsByNameAndGameSystemExcludingId(
                    club.getName(), club.getGameSystem(), null)) {
                throw new IllegalArgumentException(
                        "A club with this name already exists for " + club.getGameSystem());
            }

            Club savedClub = clubRepository.save(club);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully created club: {} (ID: {})",
                    savedClub.getName(), savedClub.getId());

            return savedClub;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to create club: {}", club.getName(), e);
            throw new RuntimeException("Failed to create club", e);

        } catch (IllegalArgumentException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Club validation failed: {}", club.getName(), e);
            throw e;
        }
    }

    public Optional<Club> updateClubProfile(Long id, ClubUpdateDTO updateDTO) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating club profile: {} with DTO: {}", id, updateDTO);

            Optional<Club> existingClubOpt = clubRepository.findByIdAndDeletedTimestampIsNull(id);

            if (existingClubOpt.isPresent()) {
                Club existingClub = existingClubOpt.get();

                // Update only the editable fields (gameSystem is NOT changed)
                existingClub.setName(updateDTO.getName());
                existingClub.setDescription(updateDTO.getDescription());
                existingClub.setContactEmail(updateDTO.getContactEmail());
                existingClub.setCountryCode(updateDTO.getCountryCode());
                existingClub.setProvinceCode(updateDTO.getProvinceCode());
                existingClub.setCity(updateDTO.getCity());
                existingClub.setPostalCode(updateDTO.getPostalCode());
                
                // Update logo if provided
                if (updateDTO.getLogoUrl() != null) {
                    existingClub.setLogoUrl(updateDTO.getLogoUrl());
                }

                Club savedClub = clubRepository.save(existingClub);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("clubs", "UPDATE", true,
                        "Updated club: " + savedClub.getName());
                loggingService.logPerformance("DB_UPDATE_CLUB", duration,
                        Map.of("clubId", id.toString()));

                logger.info("Successfully updated club profile: {} (ID: {})",
                        savedClub.getName(), id);

                return Optional.of(savedClub);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("clubs", "UPDATE", false,
                        "Club not found for update");

                logger.warn("Active club not found for update: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("clubs", "UPDATE", false,
                    "Database error: " + e.getMessage());
            loggingService.logError("DB_UPDATE_CLUB", e,
                    Map.of("clubId", id.toString(), "duration", String.valueOf(duration)));

            logger.error("Failed to update club: {}", id, e);
            throw new RuntimeException("Failed to update club", e);
        }
    }

    // Legacy method - kept for backward compatibility if needed elsewhere
    public Optional<Club> updateClub(Long id, Club updatedClub) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating club: {}", id);

            Optional<Club> existingClubOpt = clubRepository.findByIdAndDeletedTimestampIsNull(id);

            if (existingClubOpt.isPresent()) {
                Club existingClub = existingClubOpt.get();
                String oldName = existingClub.getName();

                // Validate updated club
                validateClub(updatedClub, id);

                // Check for duplicate name in the same game system (excluding current club)
                if (clubRepository.existsByNameAndGameSystemExcludingId(
                        updatedClub.getName(), updatedClub.getGameSystem(), id)) {
                    throw new IllegalArgumentException(
                            "A club with this name already exists for " + updatedClub.getGameSystem());
                }

                // Update fields
                existingClub.setName(updatedClub.getName());
                existingClub.setGameSystem(updatedClub.getGameSystem());
                existingClub.setContactEmail(updatedClub.getContactEmail());
                existingClub.setCountryCode(updatedClub.getCountryCode());
                existingClub.setProvinceCode(updatedClub.getProvinceCode());
                existingClub.setCity(updatedClub.getCity());
                existingClub.setPostalCode(updatedClub.getPostalCode());
                // Note: ownerId typically shouldn't be changed, but if needed:
                // existingClub.setOwnerId(updatedClub.getOwnerId());

                Club savedClub = clubRepository.save(existingClub);

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Successfully updated club: {} (ID: {})",
                        savedClub.getName(), id);

                return Optional.of(savedClub);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                logger.warn("Active club not found for update: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to update club: {}", id, e);
            throw new RuntimeException("Failed to update club", e);
        }
    }

    public boolean softDeleteClub(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Soft deleting club: {}", id);

            Optional<Club> clubOpt = clubRepository.findByIdAndDeletedTimestampIsNull(id);

            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                club.markAsDeleted();
                clubRepository.save(club);

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Successfully soft deleted club: {} (ID: {})",
                        club.getName(), id);
                return true;
            } else {
                long duration = System.currentTimeMillis() - startTime;
                logger.warn("Active club not found for soft deletion: {}", id);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed to soft delete club: {}", id, e);
            throw new RuntimeException("Failed to delete club", e);
        }
    }

    // Search and filter methods
    public List<Club> getClubsByOwner(Long ownerId) {
        logger.debug("Fetching clubs by owner: {}", ownerId);
        return clubRepository.findByOwnerIdAndDeletedTimestampIsNull(ownerId);
    }

    public List<Club> getClubsByGameSystem(String gameSystem) {
        logger.debug("Fetching clubs by game system: {}", gameSystem);
        return clubRepository.findByGameSystemAndDeletedTimestampIsNull(gameSystem);
    }

    public List<Club> getClubsByLocation(String countryCode, String provinceCode, String city) {
        logger.debug("Fetching clubs by location: {}, {}, {}", countryCode, provinceCode, city);

        if (city != null) {
            return clubRepository.findByCountryCodeAndProvinceCodeAndCityAndDeletedTimestampIsNull(
                    countryCode, provinceCode, city);
        } else if (provinceCode != null) {
            return clubRepository.findByCountryCodeAndProvinceCodeAndDeletedTimestampIsNull(
                    countryCode, provinceCode);
        } else {
            return clubRepository.findByCountryCodeAndDeletedTimestampIsNull(countryCode);
        }
    }

    public List<Club> searchClubsByName(String name) {
        logger.debug("Searching clubs by name: {}", name);
        return clubRepository.findByNameContainingIgnoreCaseAndDeletedTimestampIsNull(name);
    }

    private void validateClub(Club club, Long excludeId) {
        if (club.getName() == null || club.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Club name cannot be empty");
        }

        if (club.getName().length() > 100) {
            throw new IllegalArgumentException("Club name cannot exceed 100 characters");
        }

        if (club.getGameSystem() == null || club.getGameSystem().trim().isEmpty()) {
            throw new IllegalArgumentException("Game system cannot be empty");
        }

        if (club.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }

        if (club.getCountryCode() == null || club.getCountryCode().length() != 2) {
            throw new IllegalArgumentException("Country code must be exactly 2 characters");
        }

        if (club.getCity() == null || club.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }

        // Email validation is handled by @Email annotation, but we can add additional checks
        if (club.getContactEmail() != null && !club.getContactEmail().trim().isEmpty()) {
            if (club.getContactEmail().length() > 100) {
                throw new IllegalArgumentException("Contact email cannot exceed 100 characters");
            }
        }

        logger.debug("Club validation passed for: {}", club.getName());
    }
}