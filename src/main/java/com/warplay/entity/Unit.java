package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "units")
public class Unit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "force_id", nullable = false)
    private Long forceId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "data_sheet", nullable = false, length = 200)
    private String dataSheet;
    
    @Column(name = "model_count", nullable = false)
    private Integer modelCount;
    
    
    @Column(name = "unit_type", length = 50)
    private String unitType;
    
    @Column(name = "points")
    private Integer points;
    
    @Column(name = "crusade_points")
    private Integer crusadePoints;
    
    @Column(name = "wargear", columnDefinition = "TEXT")
    private String wargear;
    
    @Column(name = "enhancements", columnDefinition = "TEXT")
    private String enhancements;
    
    @Column(name = "relics", columnDefinition = "TEXT")
    private String relics;
    
    @Column(name = "battle_traits", columnDefinition = "TEXT")
    private String battleTraits;
    
    @Column(name = "battle_scars", columnDefinition = "TEXT")
    private String battleScars;
    
    @Column(name = "battle_count")
    private Integer battleCount;
    
    @Column(name = "xp")
    private Integer xp;
    
    @Column(name = "kill_count")
    private Integer killCount;
    
    @Column(name = "times_killed")
    private Integer timesKilled;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "notable_history", columnDefinition = "TEXT")
    private String notableHistory;
    
    @Column(name = "mfm_version", length = 20)
    private String mfmVersion;
    
    @Column(name = "rank", length = 50)
    private String rank;
    
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
    
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;
    
    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;
    
    @Column(name = "deleted_timestamp")
    private LocalDateTime deletedTimestamp;
    
    @PrePersist
    protected void onCreate() {
        createdTimestamp = LocalDateTime.now();
        updatedTimestamp = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedTimestamp = LocalDateTime.now();
    }
    
    // Constructors
    public Unit() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getForceId() {
        return forceId;
    }
    
    public void setForceId(Long forceId) {
        this.forceId = forceId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDataSheet() {
        return dataSheet;
    }
    
    public void setDataSheet(String dataSheet) {
        this.dataSheet = dataSheet;
    }
    
    public Integer getModelCount() {
        return modelCount;
    }
    
    public void setModelCount(Integer modelCount) {
        this.modelCount = modelCount;
    }
    
    
    public String getUnitType() {
        return unitType;
    }
    
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Integer getCrusadePoints() {
        return crusadePoints;
    }
    
    public void setCrusadePoints(Integer crusadePoints) {
        this.crusadePoints = crusadePoints;
    }
    
    public String getWargear() {
        return wargear;
    }
    
    public void setWargear(String wargear) {
        this.wargear = wargear;
    }
    
    public String getEnhancements() {
        return enhancements;
    }
    
    public void setEnhancements(String enhancements) {
        this.enhancements = enhancements;
    }
    
    public String getRelics() {
        return relics;
    }
    
    public void setRelics(String relics) {
        this.relics = relics;
    }
    
    public String getBattleTraits() {
        return battleTraits;
    }
    
    public void setBattleTraits(String battleTraits) {
        this.battleTraits = battleTraits;
    }
    
    public String getBattleScars() {
        return battleScars;
    }
    
    public void setBattleScars(String battleScars) {
        this.battleScars = battleScars;
    }
    
    public Integer getBattleCount() {
        return battleCount;
    }
    
    public void setBattleCount(Integer battleCount) {
        this.battleCount = battleCount;
    }
    
    public Integer getXp() {
        return xp;
    }
    
    public void setXp(Integer xp) {
        this.xp = xp;
    }
    
    public Integer getKillCount() {
        return killCount;
    }
    
    public void setKillCount(Integer killCount) {
        this.killCount = killCount;
    }
    
    public Integer getTimesKilled() {
        return timesKilled;
    }
    
    public void setTimesKilled(Integer timesKilled) {
        this.timesKilled = timesKilled;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getNotableHistory() {
        return notableHistory;
    }
    
    public void setNotableHistory(String notableHistory) {
        this.notableHistory = notableHistory;
    }
    
    public String getMfmVersion() {
        return mfmVersion;
    }
    
    public void setMfmVersion(String mfmVersion) {
        this.mfmVersion = mfmVersion;
    }
    
    public String getRank() {
        return rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
    
    public LocalDateTime getDeletedTimestamp() {
        return deletedTimestamp;
    }
    
    public void setDeletedTimestamp(LocalDateTime deletedTimestamp) {
        this.deletedTimestamp = deletedTimestamp;
    }
}
