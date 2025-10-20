package com.warplay.dto;

import com.warplay.entity.MfmFaction;

public class MfmFactionResponse {
    private Long id;
    private String name;
    private String supergroup;
    private String allyTo;
    private String mfmVersion;
    
    // Constructor from entity
    public MfmFactionResponse(MfmFaction mfmFaction) {
        this.id = mfmFaction.getId();
        this.name = mfmFaction.getName();
        this.supergroup = mfmFaction.getSupergroup();
        this.allyTo = mfmFaction.getAllyTo();
        this.mfmVersion = mfmFaction.getMfmVersion() != null ? mfmFaction.getMfmVersion().getVersion() : null;
    }
    
    // Default constructor
    public MfmFactionResponse() {
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
    
    public String getSupergroup() {
        return supergroup;
    }
    
    public void setSupergroup(String supergroup) {
        this.supergroup = supergroup;
    }
    
    public String getAllyTo() {
        return allyTo;
    }
    
    public void setAllyTo(String allyTo) {
        this.allyTo = allyTo;
    }
    
    public String getMfmVersion() {
        return mfmVersion;
    }
    
    public void setMfmVersion(String mfmVersion) {
        this.mfmVersion = mfmVersion;
    }
}
