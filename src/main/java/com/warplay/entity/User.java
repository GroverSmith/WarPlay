package com.warplay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String googleId;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column
    private String profilePictureUrl;

    @Column
    private String discordHandle;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastLoginTimestamp;

    @Column
    private LocalDateTime deletedTimestamp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserGameSystem> userGameSystems = new ArrayList<>();


    // Constructors
    public User() {
        this.createdTimestamp = LocalDateTime.now();
        this.lastLoginTimestamp = LocalDateTime.now();
    }

    public User(String googleId, String email, String name, String profilePictureUrl) {
        this();
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getDiscordHandle() { return discordHandle; }
    public void setDiscordHandle(String discordHandle) { this.discordHandle = discordHandle; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public LocalDateTime getLastLoginTimestamp() { return lastLoginTimestamp; }
    public void setLastLoginTimestamp(LocalDateTime lastLoginTimestamp) { this.lastLoginTimestamp = lastLoginTimestamp; }

    public LocalDateTime getDeletedTimestamp() { return deletedTimestamp; }
    public void setDeletedTimestamp(LocalDateTime deletedTimestamp) { this.deletedTimestamp = deletedTimestamp; }

    public List<UserGameSystem> getUserGameSystems() { return userGameSystems; }
    public void setUserGameSystems(List<UserGameSystem> userGameSystems) { this.userGameSystems = userGameSystems; }

    // Utility methods
    public void updateLastLogin() {
        this.lastLoginTimestamp = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedTimestamp != null;
    }

    public void markAsDeleted() {
        this.deletedTimestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}