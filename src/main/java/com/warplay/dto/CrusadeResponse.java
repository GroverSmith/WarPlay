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
    private String introduction;
    private String rulesBlock1;
    private String rulesBlock2;
    private String rulesBlock3;
    private String narrativeBlock1;
    private String narrativeBlock2;
    private String narrativeBlock3;
    private String imageUrl;
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
        this.introduction = crusade.getIntroduction();
        this.rulesBlock1 = crusade.getRulesBlock1();
        this.rulesBlock2 = crusade.getRulesBlock2();
        this.rulesBlock3 = crusade.getRulesBlock3();
        this.narrativeBlock1 = crusade.getNarrativeBlock1();
        this.narrativeBlock2 = crusade.getNarrativeBlock2();
        this.narrativeBlock3 = crusade.getNarrativeBlock3();
        this.imageUrl = crusade.getImageUrl();
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

    // Utility methods
    public boolean isActive() {
        return "Active".equalsIgnoreCase(state);
    }
}
