package com.warplay.dto;

import java.util.List;

public class CreateArmyRequest {
    private Long forceId;
    private String name;
    private Integer points;
    private String armyType; // 'paste' or 'build'
    private String armyText; // For paste mode
    private String notes;
    private String imageUrl;
    private List<SelectedUnit> selectedUnits; // For build mode
    
    // Nested class for selected units in build mode
    public static class SelectedUnit {
        private Long unitId;
        private String name;
        private String dataSheet;
        private Integer modelCount;
        private Integer points;
        
        // Constructors
        public SelectedUnit() {
        }
        
        public SelectedUnit(Long unitId, String name, String dataSheet, Integer modelCount, Integer points) {
            this.unitId = unitId;
            this.name = name;
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
        
        public Integer getPoints() {
            return points;
        }
        
        public void setPoints(Integer points) {
            this.points = points;
        }
    }
    
    // Constructors
    public CreateArmyRequest() {
    }
    
    // Getters and Setters
    public Long getForceId() {
        return forceId;
    }
    
    public void setForceId(Long forceId) {
        this.forceId = forceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public List<SelectedUnit> getSelectedUnits() {
        return selectedUnits;
    }
    
    public void setSelectedUnits(List<SelectedUnit> selectedUnits) {
        this.selectedUnits = selectedUnits;
    }
}
