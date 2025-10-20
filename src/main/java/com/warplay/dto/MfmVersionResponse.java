package com.warplay.dto;

import com.warplay.entity.MfmVersion;

public class MfmVersionResponse {
    private Long id;
    private String version;
    private String date;
    private Boolean isLatest;
    
    // Constructor from entity
    public MfmVersionResponse(MfmVersion mfmVersion) {
        this.id = mfmVersion.getId();
        this.version = mfmVersion.getVersion();
        this.date = mfmVersion.getDate();
        this.isLatest = mfmVersion.getIsLatest();
    }
    
    // Default constructor
    public MfmVersionResponse() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Boolean getIsLatest() {
        return isLatest;
    }
    
    public void setIsLatest(Boolean isLatest) {
        this.isLatest = isLatest;
    }
}
