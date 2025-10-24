package com.warplay.dto;

import com.warplay.entity.MfmDetachment;
import java.util.List;
import java.util.stream.Collectors;

public class MfmDetachmentResponse {
    private Long id;
    private String name;
    private String factionName;
    private String mfmVersion;
    private List<MfmEnhancementResponse> enhancements;
    
    // Constructor from entity
    public MfmDetachmentResponse(MfmDetachment mfmDetachment) {
        this.id = mfmDetachment.getId();
        this.name = mfmDetachment.getName();
        this.factionName = mfmDetachment.getFaction() != null ? mfmDetachment.getFaction().getName() : null;
        this.mfmVersion = mfmDetachment.getFaction() != null && mfmDetachment.getFaction().getMfmVersion() != null ? 
            mfmDetachment.getFaction().getMfmVersion().getVersion() : null;
        this.enhancements = mfmDetachment.getEnhancements() != null ? 
            mfmDetachment.getEnhancements().stream().map(MfmEnhancementResponse::new).collect(Collectors.toList()) : null;
    }
    
    // Default constructor
    public MfmDetachmentResponse() {
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
    
    public List<MfmEnhancementResponse> getEnhancements() {
        return enhancements;
    }
    
    public void setEnhancements(List<MfmEnhancementResponse> enhancements) {
        this.enhancements = enhancements;
    }
}
