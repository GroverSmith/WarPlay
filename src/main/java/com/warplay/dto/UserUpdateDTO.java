package com.warplay.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating user profile
 * Contains only the fields that users can edit themselves
 */
public class UserUpdateDTO {
    
    @Size(max = 100, message = "Discord handle cannot exceed 100 characters")
    private String discordHandle;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    // Profile picture URL (can be Base64 data URL or external URL)
    private String profilePictureUrl;
    
    // Constructors
    public UserUpdateDTO() {}
    
    public UserUpdateDTO(String discordHandle, String notes, String profilePictureUrl) {
        this.discordHandle = discordHandle;
        this.notes = notes;
        this.profilePictureUrl = profilePictureUrl;
    }
    
    // Getters and Setters
    public String getDiscordHandle() {
        return discordHandle;
    }
    
    public void setDiscordHandle(String discordHandle) {
        this.discordHandle = discordHandle;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    
    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "discordHandle='" + discordHandle + '\'' +
                ", notes='" + (notes != null ? notes.substring(0, Math.min(50, notes.length())) + "..." : null) + '\'' +
                ", profilePictureUrl='" + (profilePictureUrl != null ? "present" : "null") + '\'' +
                '}';
    }
}

