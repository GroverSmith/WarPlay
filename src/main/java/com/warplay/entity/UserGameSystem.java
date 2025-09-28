package com.warplay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_game_systems",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_system_id"}))
public class UserGameSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_system_id", nullable = false)
    private GameSystem gameSystem;

    @Min(value = 1, message = "skill rating must be at least 1")
    @Max(value = 10, message = "skill rating cannot exceed 10")
    @Column(name = "skill_rating")
    private Integer skillRating;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Min(value = 0, message = "Games per year cannot be negative")
    @Column(name = "games_per_year")
    private Integer gamesPerYear;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;

    // Constructors
    public UserGameSystem() {}

    public UserGameSystem(User user, GameSystem gameSystem) {
        this.user = user;
        this.gameSystem = gameSystem;
        this.isActive = true;
    }

    public UserGameSystem(User user, GameSystem gameSystem, Integer skillRating,
                          Integer yearsExperience, Integer gamesPerYear, String notes) {
        this.user = user;
        this.gameSystem = gameSystem;
        this.skillRating = skillRating;
        this.yearsExperience = yearsExperience;
        this.gamesPerYear = gamesPerYear;
        this.notes = notes;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameSystem getGameSystem() {
        return gameSystem;
    }

    public void setGameSystem(GameSystem gameSystem) {
        this.gameSystem = gameSystem;
    }

    public Integer getskillRating() {
        return skillRating;
    }

    public void setskillRating(Integer skillRating) {
        this.skillRating = skillRating;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public Integer getGamesPerYear() {
        return gamesPerYear;
    }

    public void setGamesPerYear(Integer gamesPerYear) {
        this.gamesPerYear = gamesPerYear;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        if (isActive) {
            this.updatedTimestamp = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    // Utility methods
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void updateTimestamp() {
        this.updatedTimestamp = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedTimestamp = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedTimestamp = LocalDateTime.now();
    }

    public String getExperienceLevel() {
        if (yearsExperience == null) return "Unknown";
        if (yearsExperience == 0) return "Beginner";
        if (yearsExperience < 2) return "Novice";
        if (yearsExperience < 5) return "Intermediate";
        if (yearsExperience < 10) return "Advanced";
        return "Expert";
    }

    public String getActivityLevel() {
        if (gamesPerYear == null) return "Unknown";
        if (gamesPerYear == 0) return "Inactive";
        if (gamesPerYear < 12) return "Casual";
        if (gamesPerYear < 24) return "Regular";
        if (gamesPerYear < 52) return "Active";
        return "Very Active";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTimestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "UserGameSystem{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", gameSystemId=" + (gameSystem != null ? gameSystem.getId() : null) +
                ", skillRating=" + skillRating +
                ", yearsExperience=" + yearsExperience +
                ", gamesPerYear=" + gamesPerYear +
                ", isActive=" + isActive +
                '}';
    }
}