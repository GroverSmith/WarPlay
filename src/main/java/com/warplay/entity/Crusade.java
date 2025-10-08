package com.warplay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crusades")
public class Crusade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Crusade name is required")
    @Size(max = 200, message = "Crusade name cannot exceed 200 characters")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @NotNull(message = "Club ID is required")
    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Size(max = 100, message = "Crusade type cannot exceed 100 characters")
    @Column(name = "type", length = 100)
    private String type;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "rules_block_1", columnDefinition = "TEXT")
    private String rulesBlock1;

    @Column(name = "rules_block_2", columnDefinition = "TEXT")
    private String rulesBlock2;

    @Column(name = "rules_block_3", columnDefinition = "TEXT")
    private String rulesBlock3;

    @Column(name = "narrative_block_1", columnDefinition = "TEXT")
    private String narrativeBlock1;

    @Column(name = "narrative_block_2", columnDefinition = "TEXT")
    private String narrativeBlock2;

    @Column(name = "narrative_block_3", columnDefinition = "TEXT")
    private String narrativeBlock3;

    @CreationTimestamp
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "deleted_timestamp")
    private LocalDateTime deletedTimestamp;

    // Constructors
    public Crusade() {}

    public Crusade(String name, Long clubId, String type, String state, 
                   LocalDate startDate, LocalDate endDate, String description) {
        this.name = name;
        this.clubId = clubId;
        this.type = type;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public Crusade(String name, Long clubId, String type, String state, 
                   LocalDate startDate, LocalDate endDate, String description,
                   String introduction, String rulesBlock1, String rulesBlock2, String rulesBlock3,
                   String narrativeBlock1, String narrativeBlock2, String narrativeBlock3) {
        this.name = name;
        this.clubId = clubId;
        this.type = type;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.introduction = introduction;
        this.rulesBlock1 = rulesBlock1;
        this.rulesBlock2 = rulesBlock2;
        this.rulesBlock3 = rulesBlock3;
        this.narrativeBlock1 = narrativeBlock1;
        this.narrativeBlock2 = narrativeBlock2;
        this.narrativeBlock3 = narrativeBlock3;
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

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRulesBlock1() {
        return rulesBlock1;
    }

    public void setRulesBlock1(String rulesBlock1) {
        this.rulesBlock1 = rulesBlock1;
    }

    public String getRulesBlock2() {
        return rulesBlock2;
    }

    public void setRulesBlock2(String rulesBlock2) {
        this.rulesBlock2 = rulesBlock2;
    }

    public String getRulesBlock3() {
        return rulesBlock3;
    }

    public void setRulesBlock3(String rulesBlock3) {
        this.rulesBlock3 = rulesBlock3;
    }

    public String getNarrativeBlock1() {
        return narrativeBlock1;
    }

    public void setNarrativeBlock1(String narrativeBlock1) {
        this.narrativeBlock1 = narrativeBlock1;
    }

    public String getNarrativeBlock2() {
        return narrativeBlock2;
    }

    public void setNarrativeBlock2(String narrativeBlock2) {
        this.narrativeBlock2 = narrativeBlock2;
    }

    public String getNarrativeBlock3() {
        return narrativeBlock3;
    }

    public void setNarrativeBlock3(String narrativeBlock3) {
        this.narrativeBlock3 = narrativeBlock3;
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

    // Utility methods
    public boolean isDeleted() {
        return deletedTimestamp != null;
    }

    public void markAsDeleted() {
        this.deletedTimestamp = LocalDateTime.now();
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(state);
    }

    @Override
    public String toString() {
        return "Crusade{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", clubId=" + clubId +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
