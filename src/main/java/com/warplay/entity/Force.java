package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forces")
public class Force {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "club_id", nullable = false)
    private Long clubId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "faction", nullable = false, length = 100)
    private String faction;
    
    @Column(name = "force_type", nullable = false, length = 20)
    private String forceType;
    
    @Column(name = "sub_faction", length = 100)
    private String subFaction;
    
    @Column(name = "detachment", length = 100)
    private String detachment;
    
    @Column(name = "supply_limit")
    private Integer supplyLimit;
    
    @Column(name = "requisition_points")
    private Integer requisitionPoints;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String imageUrl;
    
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;
    
    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;
    
    @Column(name = "deleted_timestamp")
    private LocalDateTime deletedTimestamp;
    
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
    public Force() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClubId() {
        return clubId;
    }
    
    public void setClubId(Long clubId) {
        this.clubId = clubId;
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
    
    public String getForceType() {
        return forceType;
    }
    
    public void setForceType(String forceType) {
        this.forceType = forceType;
    }
    
    public String getSubFaction() {
        return subFaction;
    }
    
    public void setSubFaction(String subFaction) {
        this.subFaction = subFaction;
    }
    
    public String getDetachment() {
        return detachment;
    }
    
    public void setDetachment(String detachment) {
        this.detachment = detachment;
    }
    
    public Integer getSupplyLimit() {
        return supplyLimit;
    }
    
    public void setSupplyLimit(Integer supplyLimit) {
        this.supplyLimit = supplyLimit;
    }
    
    public Integer getRequisitionPoints() {
        return requisitionPoints;
    }
    
    public void setRequisitionPoints(Integer requisitionPoints) {
        this.requisitionPoints = requisitionPoints;
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
    
    public LocalDateTime getDeletedTimestamp() {
        return deletedTimestamp;
    }
    
    public void setDeletedTimestamp(LocalDateTime deletedTimestamp) {
        this.deletedTimestamp = deletedTimestamp;
    }
}

