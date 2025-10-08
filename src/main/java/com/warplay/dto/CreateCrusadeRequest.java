package com.warplay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateCrusadeRequest {

    @NotBlank(message = "Crusade name is required")
    @Size(max = 200, message = "Crusade name cannot exceed 200 characters")
    private String name;

    @NotNull(message = "Club ID is required")
    private Long clubId;

    @Size(max = 100, message = "Crusade type cannot exceed 100 characters")
    private String type;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    // Constructors
    public CreateCrusadeRequest() {}

    public CreateCrusadeRequest(String name, Long clubId, String type, String state,
                                LocalDate startDate, LocalDate endDate, String description) {
        this.name = name;
        this.clubId = clubId;
        this.type = type;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    // Getters and Setters
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
}
