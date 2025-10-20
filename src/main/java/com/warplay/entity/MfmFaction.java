package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mfm_factions")
public class MfmFaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mfm_version_id", nullable = false)
    private MfmVersion mfmVersion;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "supergroup", length = 50)
    private String supergroup;
    
    @Column(name = "ally_to", length = 50)
    private String allyTo;
    
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;
    
    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;
    
    @OneToMany(mappedBy = "faction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MfmUnit> units;
    
    @OneToMany(mappedBy = "faction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MfmDetachment> detachments;
    
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
    public MfmFaction() {
    }
    
    public MfmFaction(MfmVersion mfmVersion, String name, String supergroup, String allyTo) {
        this.mfmVersion = mfmVersion;
        this.name = name;
        this.supergroup = supergroup;
        this.allyTo = allyTo;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MfmVersion getMfmVersion() {
        return mfmVersion;
    }
    
    public void setMfmVersion(MfmVersion mfmVersion) {
        this.mfmVersion = mfmVersion;
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
    
    public List<MfmUnit> getUnits() {
        return units;
    }
    
    public void setUnits(List<MfmUnit> units) {
        this.units = units;
    }
    
    public List<MfmDetachment> getDetachments() {
        return detachments;
    }
    
    public void setDetachments(List<MfmDetachment> detachments) {
        this.detachments = detachments;
    }
}
