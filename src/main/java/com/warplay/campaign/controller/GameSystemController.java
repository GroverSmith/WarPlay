package com.warplay.campaign.controller;

import com.warplay.campaign.entity.GameSystem;
import com.warplay.campaign.repository.GameSystemRepository;
import com.warplay.campaign.repository.UserGameSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/game-systems")
public class GameSystemController {

    @Autowired
    private GameSystemRepository gameSystemRepository;

    @Autowired
    private UserGameSystemRepository userGameSystemRepository;

    // Get all game systems
    @GetMapping
    public List<GameSystem> getAllGameSystems() {
        return gameSystemRepository.findAllOrderedByName();
    }

    // Get game system by ID
    @GetMapping("/{id}")
    public ResponseEntity<GameSystem> getGameSystemById(@PathVariable Long id) {
        Optional<GameSystem> gameSystem = gameSystemRepository.findById(id);
        return gameSystem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new game system
    @PostMapping
    public ResponseEntity<GameSystem> createGameSystem(@RequestBody GameSystem gameSystem) {
        // Check if name already exists
        if (gameSystemRepository.existsByName(gameSystem.getName())) {
            return ResponseEntity.badRequest().build();
        }

        GameSystem saved = gameSystemRepository.save(gameSystem);
        return ResponseEntity.ok(saved);
    }

    // Update game system
    @PutMapping("/{id}")
    public ResponseEntity<GameSystem> updateGameSystem(@PathVariable Long id, @RequestBody GameSystem gameSystemDetails) {
        Optional<GameSystem> optionalGameSystem = gameSystemRepository.findById(id);

        if (optionalGameSystem.isPresent()) {
            GameSystem gameSystem = optionalGameSystem.get();
            gameSystem.setName(gameSystemDetails.getName());
            gameSystem.setShortName(gameSystemDetails.getShortName());
            gameSystem.setDescription(gameSystemDetails.getDescription());
            gameSystem.setPublisher(gameSystemDetails.getPublisher());
            gameSystem.setIconUrl(gameSystemDetails.getIconUrl());

            GameSystem updated = gameSystemRepository.save(gameSystem);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.notFound().build();
    }

    // Delete game system (only if no users are associated)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameSystem(@PathVariable Long id) {
        Optional<GameSystem> optionalGameSystem = gameSystemRepository.findById(id);

        if (optionalGameSystem.isPresent()) {
            // Check if any users are associated with this game system
            Long userCount = userGameSystemRepository.countActivePlayersByGameSystemId(id);
            if (userCount > 0) {
                return ResponseEntity.badRequest().build(); // Cannot delete, users exist
            }

            gameSystemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Search game systems by name
    @GetMapping("/search")
    public List<GameSystem> searchGameSystems(@RequestParam String query) {
        return gameSystemRepository.searchByNameContaining(query);
    }

    // Get game systems by publisher
    @GetMapping("/publisher/{publisher}")
    public List<GameSystem> getGameSystemsByPublisher(@PathVariable String publisher) {
        return gameSystemRepository.findByPublisher(publisher);
    }

    // Get statistics for a game system
    @GetMapping("/{id}/stats")
    public ResponseEntity<Object> getGameSystemStats(@PathVariable Long id) {
        Optional<GameSystem> gameSystem = gameSystemRepository.findById(id);

        if (gameSystem.isPresent()) {
            Long playerCount = userGameSystemRepository.countActivePlayersByGameSystemId(id);
            Double avgSkillRating = userGameSystemRepository.getAverageSkillRatingByGameSystemId(id);

            return ResponseEntity.ok(new Object() {
                public final String gameSystemName = gameSystem.get().getName();
                public final Long activePlayerCount = playerCount;
                public final Double averageSkillRating = avgSkillRating;
            });
        }

        return ResponseEntity.notFound().build();
    }
}