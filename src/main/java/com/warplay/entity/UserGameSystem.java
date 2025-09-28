package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_game_systems",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_system_id"}))
public class UserGameSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_system_id", nullable = false)
    private GameSystem gameSystem;

    @Column
    private Integer skillRating; // 1-10 self-assessment

    @Column
    private Integer yearsExperience;

    @Column
    private Integer gamesPerYear;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedTimestamp;

    // Constructors
    public UserGameSystem() {
        this.createdTimestamp = LocalDateTime.now();
        this.lastUpdatedTimestamp = LocalDateTime.now();
        this.isActive = true;
    }

    public UserGameSystem(User user, GameSystem gameSystem) {
        this();
        this.user = user;
        this.gameSystem = gameSystem;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public GameSystem getGameSystem() { return gameSystem; }
    public void setGameSystem(GameSystem gameSystem) { this.gameSystem = gameSystem; }

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

    public LocalDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public LocalDateTime getLastUpdatedTimestamp() { return lastUpdatedTimestamp; }
    public void setLastUpdatedTimestamp(LocalDateTime lastUpdatedTimestamp) { this.lastUpdatedTimestamp = lastUpdatedTimestamp; }

    // Utility methods
    public void updateTimestamp() {
        this.lastUpdatedTimestamp = LocalDateTime.now();
    }
}