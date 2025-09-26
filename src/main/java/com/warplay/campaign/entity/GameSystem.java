package com.warplay.campaign.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_systems")
public class GameSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String shortName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String publisher;

    @Column
    private String iconUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @OneToMany(mappedBy = "gameSystem", cascade = CascadeType.ALL)
    private List<UserGameSystem> userGameSystems = new ArrayList<>();

    // Constructors
    public GameSystem() {
        this.createdTimestamp = LocalDateTime.now();
    }

    public GameSystem(String name, String shortName, String description, String publisher) {
        this();
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.publisher = publisher;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public LocalDateTime getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) { this.createdTimestamp = createdTimestamp; }

    public List<UserGameSystem> getUserGameSystems() { return userGameSystems; }
    public void setUserGameSystems(List<UserGameSystem> userGameSystems) { this.userGameSystems = userGameSystems; }
}