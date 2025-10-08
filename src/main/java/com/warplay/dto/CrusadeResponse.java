package com.warplay.dto;

import com.warplay.entity.Crusade;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CrusadeResponse {
    private Long id;
    private String name;
    private Long clubId;
    private String type;
    private String state;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private LocalDateTime createdTimestamp;

    // Constructors
    public CrusadeResponse() {}

    public CrusadeResponse(Crusade crusade) {
        this.id = crusade.getId();
        this.name = crusade.getName();
        this.clubId = crusade.getClubId();
        this.type = crusade.getType();
        this.state = crusade.getState();
        this.startDate = crusade.getStartDate();
        this.endDate = crusade.getEndDate();
        this.description = crusade.getDescription();
        this.createdTimestamp = crusade.getCreatedTimestamp();
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

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    // Utility methods
    public boolean isActive() {
        return "Active".equalsIgnoreCase(state);
    }
}
