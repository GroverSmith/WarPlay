package com.warplay.service;

import com.warplay.dto.CreateCrusadeRequest;
import com.warplay.dto.CrusadeResponse;
import com.warplay.dto.UpdateCrusadeRequest;
import com.warplay.entity.Crusade;
import com.warplay.repository.CrusadeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CrusadeService {

    private static final Logger logger = LoggerFactory.getLogger(CrusadeService.class);

    @Autowired
    private CrusadeRepository crusadeRepository;

    @Autowired
    private LoggingService loggingService;

    public List<CrusadeResponse> getAllActiveCrusades() {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching all active crusades from database");

            List<Crusade> crusades = crusadeRepository.findByDeletedTimestampIsNull();
            List<CrusadeResponse> response = crusades.stream()
                    .map(CrusadeResponse::new)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_ALL_ACTIVE", true,
                "Retrieved " + response.size() + " active crusades");

            logger.info("Successfully retrieved {} active crusades from database", response.size());
            return response;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_ALL_ACTIVE", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve active crusades from database", e);
            throw new RuntimeException("Failed to retrieve crusades", e);
        }
    }

    public Optional<CrusadeResponse> getActiveCrusadeById(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active crusade by ID: {}", id);

            Optional<Crusade> crusade = crusadeRepository.findByIdAndDeletedTimestampIsNull(id);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_BY_ID", true,
                "Retrieved crusade by ID: " + id);

            if (crusade.isPresent()) {
                logger.info("Successfully retrieved crusade by ID: {}", id);
                return Optional.of(new CrusadeResponse(crusade.get()));
            } else {
                logger.warn("Active crusade not found by ID: {}", id);
                return Optional.empty();
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_BY_ID", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve crusade by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve crusade", e);
        }
    }

    public List<CrusadeResponse> getCrusadesByClub(Long clubId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching crusades by club: {}", clubId);

            List<Crusade> crusades = crusadeRepository.findByClubIdAndDeletedTimestampIsNull(clubId);
            List<CrusadeResponse> response = crusades.stream()
                    .map(CrusadeResponse::new)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_BY_CLUB", true,
                "Retrieved " + response.size() + " crusades for club: " + clubId);

            logger.info("Successfully retrieved {} crusades for club: {}", response.size(), clubId);
            return response;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_BY_CLUB", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve crusades for club: {}", clubId, e);
            throw new RuntimeException("Failed to retrieve crusades for club", e);
        }
    }

    public List<CrusadeResponse> getActiveCrusadesByClub(Long clubId) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Fetching active crusades by club: {}", clubId);

            List<Crusade> crusades = crusadeRepository.findActiveCrusadesByClub(clubId);
            List<CrusadeResponse> response = crusades.stream()
                    .map(CrusadeResponse::new)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_ACTIVE_BY_CLUB", true,
                "Retrieved " + response.size() + " active crusades for club: " + clubId);

            logger.info("Successfully retrieved {} active crusades for club: {}", response.size(), clubId);
            return response;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SELECT_ACTIVE_BY_CLUB", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to retrieve active crusades for club: {}", clubId, e);
            throw new RuntimeException("Failed to retrieve active crusades for club", e);
        }
    }

    public CrusadeResponse createCrusade(CreateCrusadeRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Creating new crusade: {}", request.getName());

            // Validate date range
            if (request.getStartDate() != null && request.getEndDate() != null) {
                if (request.getEndDate().isBefore(request.getStartDate())) {
                    throw new IllegalArgumentException("End date cannot be before start date");
                }
            }

            // Check for duplicate name within club
            if (crusadeRepository.existsByNameAndClubExcludingId(
                    request.getName(), request.getClubId(), null)) {
                throw new IllegalArgumentException(
                    "A crusade with this name already exists in the club");
            }

            // Create entity from request
            Crusade crusade = new Crusade(
                    request.getName(),
                    request.getClubId(),
                    request.getType(),
                    request.getState(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getDescription(),
                    request.getIntroduction(),
                    request.getRulesBlock1(),
                    request.getRulesBlock2(),
                    request.getRulesBlock3(),
                    request.getNarrativeBlock1(),
                    request.getNarrativeBlock2(),
                    request.getNarrativeBlock3()
            );

            Crusade savedCrusade = crusadeRepository.save(crusade);

            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "INSERT", true,
                "Created crusade: " + savedCrusade.getName());

            logger.info("Successfully created crusade: {} (ID: {})",
                savedCrusade.getName(), savedCrusade.getId());

            return new CrusadeResponse(savedCrusade);

        } catch (IllegalArgumentException e) {
            loggingService.logDatabaseOperation("crusades", "INSERT", false,
                "Validation error: " + e.getMessage());
            logger.warn("Crusade creation failed - validation error: {}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "INSERT", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to create crusade: {}", request.getName(), e);
            throw new RuntimeException("Failed to create crusade", e);
        }
    }

    public Optional<CrusadeResponse> updateCrusade(Long id, UpdateCrusadeRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Updating crusade: {}", id);

            Optional<Crusade> existingCrusade = crusadeRepository.findByIdAndDeletedTimestampIsNull(id);

            if (existingCrusade.isPresent()) {
                Crusade crusadeToUpdate = existingCrusade.get();

                // Validate date range
                if (request.getStartDate() != null && request.getEndDate() != null) {
                    if (request.getEndDate().isBefore(request.getStartDate())) {
                        throw new IllegalArgumentException("End date cannot be before start date");
                    }
                }

                // Check for duplicate name within club (excluding current crusade)
                if (crusadeRepository.existsByNameAndClubExcludingId(
                        request.getName(), crusadeToUpdate.getClubId(), id)) {
                    throw new IllegalArgumentException(
                        "A crusade with this name already exists in the club");
                }

                // Update fields
                crusadeToUpdate.setName(request.getName());
                crusadeToUpdate.setType(request.getType());
                crusadeToUpdate.setState(request.getState());
                crusadeToUpdate.setStartDate(request.getStartDate());
                crusadeToUpdate.setEndDate(request.getEndDate());
                crusadeToUpdate.setDescription(request.getDescription());
                crusadeToUpdate.setIntroduction(request.getIntroduction());
                crusadeToUpdate.setRulesBlock1(request.getRulesBlock1());
                crusadeToUpdate.setRulesBlock2(request.getRulesBlock2());
                crusadeToUpdate.setRulesBlock3(request.getRulesBlock3());
                crusadeToUpdate.setNarrativeBlock1(request.getNarrativeBlock1());
                crusadeToUpdate.setNarrativeBlock2(request.getNarrativeBlock2());
                crusadeToUpdate.setNarrativeBlock3(request.getNarrativeBlock3());

                Crusade updatedCrusade = crusadeRepository.save(crusadeToUpdate);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("crusades", "UPDATE", true,
                    "Updated crusade ID: " + id);

                logger.info("Successfully updated crusade: {}", id);
                return Optional.of(new CrusadeResponse(updatedCrusade));

            } else {
                logger.warn("Active crusade not found for update: {}", id);
                return Optional.empty();
            }

        } catch (IllegalArgumentException e) {
            loggingService.logDatabaseOperation("crusades", "UPDATE", false,
                "Validation error: " + e.getMessage());
            logger.warn("Crusade update failed - validation error: {}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "UPDATE", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to update crusade: {}", id, e);
            throw new RuntimeException("Failed to update crusade", e);
        }
    }

    public boolean softDeleteCrusade(Long id) {
        long startTime = System.currentTimeMillis();

        try {
            logger.debug("Soft deleting crusade: {}", id);

            Optional<Crusade> existingCrusade = crusadeRepository.findByIdAndDeletedTimestampIsNull(id);

            if (existingCrusade.isPresent()) {
                Crusade crusade = existingCrusade.get();
                crusade.markAsDeleted();
                crusadeRepository.save(crusade);

                long duration = System.currentTimeMillis() - startTime;
                loggingService.logDatabaseOperation("crusades", "SOFT_DELETE", true,
                    "Soft deleted crusade ID: " + id);

                logger.info("Successfully soft deleted crusade: {}", id);
                return true;

            } else {
                logger.warn("Active crusade not found for deletion: {}", id);
                return false;
            }

        } catch (DataAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            loggingService.logDatabaseOperation("crusades", "SOFT_DELETE", false,
                "Database error: " + e.getMessage());

            logger.error("Failed to soft delete crusade: {}", id, e);
            throw new RuntimeException("Failed to delete crusade", e);
        }
    }

    public List<CrusadeResponse> searchCrusadesByName(String name) {
        try {
            logger.debug("Searching crusades by name: {}", name);

            List<Crusade> crusades = crusadeRepository.findByNameContainingIgnoreCaseAndDeletedTimestampIsNull(name);
            List<CrusadeResponse> response = crusades.stream()
                    .map(CrusadeResponse::new)
                    .collect(Collectors.toList());

            logger.info("Found {} crusades matching name: {}", response.size(), name);
            return response;

        } catch (DataAccessException e) {
            logger.error("Failed to search crusades by name: {}", name, e);
            throw new RuntimeException("Failed to search crusades", e);
        }
    }
}
