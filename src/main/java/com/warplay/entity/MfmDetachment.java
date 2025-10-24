package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mfm_detachments")
public class MfmDetachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id", nullable = false)
    private MfmFaction faction;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;
    
    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;
    
    @OneToMany(mappedBy = "detachment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MfmEnhancement> enhancements;
    
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
    public MfmDetachment() {
    }
    
    public MfmDetachment(MfmFaction faction, String name) {
        this.faction = faction;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MfmFaction getFaction() {
        return faction;
    }
    
    public void setFaction(MfmFaction faction) {
        this.faction = faction;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public List<MfmEnhancement> getEnhancements() {
        return enhancements;
    }
    
    public void setEnhancements(List<MfmEnhancement> enhancements) {
        this.enhancements = enhancements;
    }
}
