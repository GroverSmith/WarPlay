package com.warplay.dto;

import com.warplay.entity.MfmUnitVariant;

public class MfmUnitVariantResponse {
    private Long id;
    private Integer modelCount;
    private Integer points;
    private String unitName;
    private String factionName;
    private String mfmVersion;
    
    // Constructor from entity
    public MfmUnitVariantResponse(MfmUnitVariant mfmUnitVariant) {
        this.id = mfmUnitVariant.getId();
        this.modelCount = mfmUnitVariant.getModelCount();
        this.points = mfmUnitVariant.getPoints();
        this.unitName = mfmUnitVariant.getUnit() != null ? mfmUnitVariant.getUnit().getName() : null;
        this.factionName = mfmUnitVariant.getUnit() != null && mfmUnitVariant.getUnit().getFaction() != null ? 
            mfmUnitVariant.getUnit().getFaction().getName() : null;
        this.mfmVersion = mfmUnitVariant.getUnit() != null && mfmUnitVariant.getUnit().getFaction() != null && 
            mfmUnitVariant.getUnit().getFaction().getMfmVersion() != null ? 
            mfmUnitVariant.getUnit().getFaction().getMfmVersion().getVersion() : null;
    }
    
    // Default constructor
    public MfmUnitVariantResponse() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getUnitName() {
        return unitName;
    }
    
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    
    public String getFactionName() {
        return factionName;
    }
    
    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }
    
    public String getMfmVersion() {
        return mfmVersion;
    }
    
    public void setMfmVersion(String mfmVersion) {
        this.mfmVersion = mfmVersion;
    }
}
