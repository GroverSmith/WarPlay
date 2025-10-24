package com.warplay.dto;

import com.warplay.entity.Unit;
import com.warplay.entity.User;
import com.warplay.entity.Force;

import java.time.LocalDateTime;

public class UnitResponse {
    private Long id;
    private Long forceId;
    private String forceName;
    private Long userId;
    private String playerName;
    private String name;
    private String dataSheet;
    private Integer modelCount;
    private String unitType;
    private Integer points;
    private Integer crusadePoints;
    private String wargear;
    private String enhancements;
    private String relics;
    private String battleTraits;
    private String battleScars;
    private Integer battleCount;
    private Integer xp;
    private Integer killCount;
    private Integer timesKilled;
    private String description;
    private String notes;
    private String notableHistory;
    private String mfmVersion;
    private String rank;
    private String imageUrl;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    
    // Constructor from Unit entity
    public UnitResponse(Unit unit) {
        this.id = unit.getId();
        this.forceId = unit.getForceId();
        this.userId = unit.getUserId();
        this.name = unit.getName();
        this.dataSheet = unit.getDataSheet();
        this.modelCount = unit.getModelCount();
        this.unitType = unit.getUnitType();
        this.points = unit.getPoints();
        this.crusadePoints = unit.getCrusadePoints();
        this.wargear = unit.getWargear();
        this.enhancements = unit.getEnhancements();
        this.relics = unit.getRelics();
        this.battleTraits = unit.getBattleTraits();
        this.battleScars = unit.getBattleScars();
        this.battleCount = unit.getBattleCount();
        this.xp = unit.getXp();
        this.killCount = unit.getKillCount();
        this.timesKilled = unit.getTimesKilled();
        this.description = unit.getDescription();
        this.notes = unit.getNotes();
        this.notableHistory = unit.getNotableHistory();
        this.mfmVersion = unit.getMfmVersion();
        this.rank = unit.getRank() != null ? unit.getRank() : "Battle-ready";
        this.imageUrl = unit.getImageUrl();
        this.createdTimestamp = unit.getCreatedTimestamp();
        this.updatedTimestamp = unit.getUpdatedTimestamp();
    }
    
    // Constructor from Unit and User
    public UnitResponse(Unit unit, User user) {
        this(unit);
        this.playerName = user != null ? user.getName() : null;
    }
    
    // Constructor from Unit, User, and Force
    public UnitResponse(Unit unit, User user, Force force) {
        this(unit, user);
        this.forceName = force != null ? force.getName() : null;
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
    
    public String getForceName() {
        return forceName;
    }
    
    public void setForceName(String forceName) {
        this.forceName = forceName;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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
}
