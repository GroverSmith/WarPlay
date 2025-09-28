package com.warplay.controller;

import com.warplay.entity.UserGameSystem;
import com.warplay.entity.User;
import com.warplay.entity.GameSystem;
import com.warplay.repository.UserGameSystemRepository;
import com.warplay.repository.UserRepository;
import com.warplay.repository.GameSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-game-systems")
public class UserGameSystemController {

    @Autowired
    private UserGameSystemRepository userGameSystemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSystemRepository gameSystemRepository;

    // Get all user-game system relationships
    @GetMapping
    public List<UserGameSystem> getAllUserGameSystems() {
        return userGameSystemRepository.findAll();
    }

    // Get user-game systems by user ID
    @GetMapping("/user/{userId}")
    public List<UserGameSystem> getUserGameSystemsByUserId(@PathVariable Long userId) {
        return userGameSystemRepository.findByUserId(userId);
    }

    // Get active user-game systems by user ID
    @GetMapping("/user/{userId}/active")
    public List<UserGameSystem> getActiveUserGameSystemsByUserId(@PathVariable Long userId) {
        return userGameSystemRepository.findActiveByUserId(userId);
    }

    // Get users by game system ID
    @GetMapping("/game-system/{gameSystemId}")
    public List<UserGameSystem> getUsersByGameSystemId(@PathVariable Long gameSystemId) {
        return userGameSystemRepository.findByGameSystemId(gameSystemId);
    }

    // Get active users by game system ID
    @GetMapping("/game-system/{gameSystemId}/active")
    public List<UserGameSystem> getActiveUsersByGameSystemId(@PathVariable Long gameSystemId) {
        return userGameSystemRepository.findActiveByGameSystemId(gameSystemId);
    }

    // Get specific user-game system relationship
    @GetMapping("/user/{userId}/game-system/{gameSystemId}")
    public ResponseEntity<UserGameSystem> getUserGameSystem(@PathVariable Long userId, @PathVariable Long gameSystemId) {
        Optional<UserGameSystem> userGameSystem = userGameSystemRepository.findByUserIdAndGameSystemId(userId, gameSystemId);
        return userGameSystem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create or update user-game system relationship
    @PostMapping
    public ResponseEntity<UserGameSystem> createOrUpdateUserGameSystem(@RequestBody UserGameSystemRequest request) {
        Optional<User> user = userRepository.findByIdActive(request.getUserId());
        Optional<GameSystem> gameSystem = gameSystemRepository.findById(request.getGameSystemId());

        if (user.isEmpty() || gameSystem.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if relationship already exists
        Optional<UserGameSystem> existing = userGameSystemRepository.findByUserIdAndGameSystemId(
                request.getUserId(), request.getGameSystemId());

        UserGameSystem userGameSystem;
        if (existing.isPresent()) {
            // Update existing
            userGameSystem = existing.get();
            userGameSystem.setSkillRating(request.getSkillRating());
            userGameSystem.setYearsExperience(request.getYearsExperience());
            userGameSystem.setGamesPerYear(request.getGamesPerYear());
            userGameSystem.setNotes(request.getNotes());
            userGameSystem.setIsActive(request.getIsActive());
            userGameSystem.updateTimestamp();
        } else {
            // Create new
            userGameSystem = new UserGameSystem(user.get(), gameSystem.get());
            userGameSystem.setSkillRating(request.getSkillRating());
            userGameSystem.setYearsExperience(request.getYearsExperience());
            userGameSystem.setGamesPerYear(request.getGamesPerYear());
            userGameSystem.setNotes(request.getNotes());
            userGameSystem.setIsActive(request.getIsActive());
        }

        UserGameSystem saved = userGameSystemRepository.save(userGameSystem);
        return ResponseEntity.ok(saved);
    }

    // Update user-game system relationship
    @PutMapping("/{id}")
    public ResponseEntity<UserGameSystem> updateUserGameSystem(@PathVariable Long id, @RequestBody UserGameSystemRequest request) {
        Optional<UserGameSystem> optionalUserGameSystem = userGameSystemRepository.findById(id);

        if (optionalUserGameSystem.isPresent()) {
            UserGameSystem userGameSystem = optionalUserGameSystem.get();
            userGameSystem.setSkillRating(request.getSkillRating());
            userGameSystem.setYearsExperience(request.getYearsExperience());
            userGameSystem.setGamesPerYear(request.getGamesPerYear());
            userGameSystem.setNotes(request.getNotes());
            userGameSystem.setIsActive(request.getIsActive());
            userGameSystem.updateTimestamp();

            UserGameSystem updated = userGameSystemRepository.save(userGameSystem);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.notFound().build();
    }

    // Delete user-game system relationship
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserGameSystem(@PathVariable Long id) {
        if (userGameSystemRepository.existsById(id)) {
            userGameSystemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Set user-game system as inactive
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserGameSystem> deactivateUserGameSystem(@PathVariable Long id) {
        Optional<UserGameSystem> optionalUserGameSystem = userGameSystemRepository.findById(id);

        if (optionalUserGameSystem.isPresent()) {
            UserGameSystem userGameSystem = optionalUserGameSystem.get();
            userGameSystem.setIsActive(false);
            userGameSystem.updateTimestamp();

            UserGameSystem updated = userGameSystemRepository.save(userGameSystem);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.notFound().build();
    }

    // Inner class for request body
    public static class UserGameSystemRequest {
        private Long userId;
        private Long gameSystemId;
        private Integer skillRating;
        private Integer yearsExperience;
        private Integer gamesPerYear;
        private String notes;
        private Boolean isActive = true;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getGameSystemId() { return gameSystemId; }
        public void setGameSystemId(Long gameSystemId) { this.gameSystemId = gameSystemId; }

        public Integer getSkillRating() { return skillRating; }
        public void setSkillRating(Integer skillRating) { this.skillRating = skillRating; }

        public Integer getYearsExperience() { return yearsExperience; }
        public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

        public Integer getGamesPerYear() { return gamesPerYear; }
        public void setGamesPerYear(Integer gamesPerYear) { this.gamesPerYear = gamesPerYear; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}