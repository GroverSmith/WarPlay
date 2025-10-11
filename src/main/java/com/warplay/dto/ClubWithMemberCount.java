package com.warplay.dto;

import com.warplay.entity.Club;

import java.time.LocalDateTime;

public class ClubWithMemberCount {
    private Long id;
    private String name;
    private String gameSystem;
    private Long ownerId;
    private String contactEmail;
    private String description;
    private String countryCode;
    private String provinceCode;
    private String city;
    private String postalCode;
    private String logoUrl;
    private LocalDateTime createdTimestamp;
    private LocalDateTime deletedTimestamp;
    private Long memberCount;

    // Constructors
    public ClubWithMemberCount() {}

    public ClubWithMemberCount(Club club, Long memberCount) {
        this.id = club.getId();
        this.name = club.getName();
        this.gameSystem = club.getGameSystem();
        this.ownerId = club.getOwnerId();
        this.contactEmail = club.getContactEmail();
        this.description = club.getDescription();
        this.countryCode = club.getCountryCode();
        this.provinceCode = club.getProvinceCode();
        this.city = club.getCity();
        this.postalCode = club.getPostalCode();
        this.logoUrl = club.getLogoUrl();
        this.createdTimestamp = club.getCreatedTimestamp();
        this.deletedTimestamp = club.getDeletedTimestamp();
        this.memberCount = memberCount;
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

    public String getGameSystem() {
        return gameSystem;
    }

    public void setGameSystem(String gameSystem) {
        this.gameSystem = gameSystem;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDescription() {
        return description != null ? description : "A gaming club for war game enthusiasts. Join us for exciting campaigns and battles!";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }

    // Utility methods
    public boolean isDeleted() {
        return deletedTimestamp != null;
    }

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        address.append(city);
        if (provinceCode != null && !provinceCode.isEmpty()) {
            address.append(", ").append(provinceCode);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            address.append(" ").append(postalCode);
        }
        address.append(", ").append(countryCode);
        return address.toString();
    }
}
