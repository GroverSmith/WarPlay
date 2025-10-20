package com.warplay.dto;

import com.warplay.entity.MfmEnhancement;

public class MfmEnhancementResponse {
    private Long id;
    private String name;
    private Integer points;
    private String detachmentName;
    private String factionName;
    private String mfmVersion;
    
    // Constructor from entity
    public MfmEnhancementResponse(MfmEnhancement mfmEnhancement) {
        this.id = mfmEnhancement.getId();
        this.name = mfmEnhancement.getName();
        this.points = mfmEnhancement.getPoints();
        this.detachmentName = mfmEnhancement.getDetachment() != null ? mfmEnhancement.getDetachment().getName() : null;
        this.factionName = mfmEnhancement.getDetachment() != null && mfmEnhancement.getDetachment().getFaction() != null ? 
            mfmEnhancement.getDetachment().getFaction().getName() : null;
        this.mfmVersion = mfmEnhancement.getDetachment() != null && mfmEnhancement.getDetachment().getFaction() != null && 
            mfmEnhancement.getDetachment().getFaction().getMfmVersion() != null ? 
            mfmEnhancement.getDetachment().getFaction().getMfmVersion().getVersion() : null;
    }
    
    // Default constructor
    public MfmEnhancementResponse() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getDetachmentName() {
        return detachmentName;
    }
    
    public void setDetachmentName(String detachmentName) {
        this.detachmentName = detachmentName;
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
