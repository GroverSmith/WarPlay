package com.warplay.dto;

import com.warplay.entity.MfmUnit;
import java.util.List;
import java.util.stream.Collectors;

public class MfmUnitResponse {
    private Long id;
    private String name;
    private String unitType;
    private String factionName;
    private String mfmVersion;
    private List<MfmUnitVariantResponse> variants;
    
    // Constructor from entity
    public MfmUnitResponse(MfmUnit mfmUnit) {
        this.id = mfmUnit.getId();
        this.name = mfmUnit.getName();
        this.unitType = mfmUnit.getUnitType();
        this.factionName = mfmUnit.getFaction() != null ? mfmUnit.getFaction().getName() : null;
        this.mfmVersion = mfmUnit.getFaction() != null && mfmUnit.getFaction().getMfmVersion() != null ? 
            mfmUnit.getFaction().getMfmVersion().getVersion() : null;
        this.variants = mfmUnit.getVariants() != null ? 
            mfmUnit.getVariants().stream().map(MfmUnitVariantResponse::new).collect(Collectors.toList()) : null;
    }
    
    // Default constructor
    public MfmUnitResponse() {
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
    
    public String getUnitType() {
        return unitType;
    }
    
    public void setUnitType(String unitType) {
        this.unitType = unitType;
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
    
    public List<MfmUnitVariantResponse> getVariants() {
        return variants;
    }
    
    public void setVariants(List<MfmUnitVariantResponse> variants) {
        this.variants = variants;
    }
}
