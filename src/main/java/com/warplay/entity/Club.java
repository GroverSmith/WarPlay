package com.warplay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Club name is required")
    @Size(max = 100, message = "Club name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Game system is required")
    @Size(max = 50, message = "Game system cannot exceed 50 characters")
    @Column(name = "game_system", nullable = false, length = 50)
    private String gameSystem;

    @NotNull(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Column(name = "contact_email", length = 100)
    private String contactEmail;


    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    @Column(name = "description", nullable = true, length = 5000)
    private String description;

    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Size(max = 10, message = "Province code cannot exceed 10 characters")
    @Column(name = "province_code", length = 10)
    private String provinceCode;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @CreationTimestamp
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "deleted_timestamp")
    private LocalDateTime deletedTimestamp;

    // Constructors
    public Club() {}

    public Club(String name, String gameSystem, Long ownerId, String contactEmail, String description,
                String countryCode, String provinceCode, String city, String postalCode) {
        this.name = name;
        this.gameSystem = gameSystem;
        this.ownerId = ownerId;
        this.contactEmail = contactEmail;
        this.description = description;
        this.countryCode = countryCode;
        this.provinceCode = provinceCode;
        this.city = city;
        this.postalCode = postalCode;
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
        return description;
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

    // Utility methods
    public boolean isDeleted() {
        return deletedTimestamp != null;
    }

    public void markAsDeleted() {
        this.deletedTimestamp = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Club{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gameSystem='" + gameSystem + '\'' +
                ", city='" + city + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}