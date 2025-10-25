package com.warplay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "army_units")
public class ArmyUnit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "army_id", nullable = false)
    private Long armyId;
    
    @Column(name = "unit_id", nullable = false)
    private Long unitId;
    
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;
    
    @Column(name = "deleted_timestamp")
    private LocalDateTime deletedTimestamp;
    
    @PrePersist
    protected void onCreate() {
        createdTimestamp = LocalDateTime.now();
    }
    
    // Constructors
    public ArmyUnit() {
    }
    
    public ArmyUnit(Long armyId, Long unitId) {
        this.armyId = armyId;
        this.unitId = unitId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getArmyId() {
        return armyId;
    }
    
    public void setArmyId(Long armyId) {
        this.armyId = armyId;
    }
    
    public Long getUnitId() {
        return unitId;
    }
    
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    
    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public LocalDateTime getDeletedTimestamp() {
        return deletedTimestamp;
    }
    
    public void setDeletedTimestamp(LocalDateTime deletedTimestamp) {
        this.deletedTimestamp = deletedTimestamp;
    }
}
