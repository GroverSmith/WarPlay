package com.warplay.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ArmyResponse {
    private Long id;
    private Long forceId;
    private Long userId;
    private String name;
    private String faction;
    private String detachment;
    private String mfmVersion;
    private Integer points;
    private String armyType;
    private String armyText;
    private String notes;
    private String imageUrl;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    private List<ArmyUnitResponse> units; // For build mode
    
    // Nested class for army units
    public static class ArmyUnitResponse {
        private Long unitId;
        private String unitName;
        private String dataSheet;
        private Integer modelCount;
        private Integer points;
        
        // Constructors
        public ArmyUnitResponse() {
        }
        
        public ArmyUnitResponse(Long unitId, String unitName, String dataSheet, Integer modelCount, Integer points) {
            this.unitId = unitId;
            this.unitName = unitName;
            this.dataSheet = dataSheet;
            this.modelCount = modelCount;
            this.points = points;
        }
        
        // Getters and Setters
        public Long getUnitId() {
            return unitId;
        }
        
        public void setUnitId(Long unitId) {
            this.unitId = unitId;
        }
        
        public String getUnitName() {
            return unitName;
        }
        
        public void setUnitName(String unitName) {
            this.unitName = unitName;
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
        
        public Integer getPoints() {
            return points;
        }
        
        public void setPoints(Integer points) {
            this.points = points;
        }
    }
    
    // Constructors
    public ArmyResponse() {
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
    
    public String getFaction() {
        return faction;
    }
    
    public void setFaction(String faction) {
        this.faction = faction;
    }
    
    public String getDetachment() {
        return detachment;
    }
    
    public void setDetachment(String detachment) {
        this.detachment = detachment;
    }
    
    public String getMfmVersion() {
        return mfmVersion;
    }
    
    public void setMfmVersion(String mfmVersion) {
        this.mfmVersion = mfmVersion;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public String getArmyType() {
        return armyType;
    }
    
    public void setArmyType(String armyType) {
        this.armyType = armyType;
    }
    
    public String getArmyText() {
        return armyText;
    }
    
    public void setArmyText(String armyText) {
        this.armyText = armyText;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public List<ArmyUnitResponse> getUnits() {
        return units;
    }
    
    public void setUnits(List<ArmyUnitResponse> units) {
        this.units = units;
    }
}
