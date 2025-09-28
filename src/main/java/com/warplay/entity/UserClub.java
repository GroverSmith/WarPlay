package com.warplay.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_clubs")
public class UserClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ClubRole role = ClubRole.MEMBER;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "joined_timestamp", nullable = false, updatable = false)
    private LocalDateTime joinedTimestamp;

    @Column(name = "left_timestamp")
    private LocalDateTime leftTimestamp;

    @Column(name = "notes", length = 500)
    private String notes;

    // Constructors
    public UserClub() {}

    public UserClub(User user, Club club) {
        this.user = user;
        this.club = club;
        this.role = ClubRole.MEMBER;
        this.isActive = true;
    }

    public UserClub(User user, Club club, ClubRole role) {
        this.user = user;
        this.club = club;
        this.role = role;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public ClubRole getRole() {
        return role;
    }

    public void setRole(ClubRole role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        if (!isActive && leftTimestamp == null) {
            this.leftTimestamp = LocalDateTime.now();
        }
    }

    public LocalDateTime getJoinedTimestamp() {
        return joinedTimestamp;
    }

    public void setJoinedTimestamp(LocalDateTime joinedTimestamp) {
        this.joinedTimestamp = joinedTimestamp;
    }

    public LocalDateTime getLeftTimestamp() {
        return leftTimestamp;
    }

    public void setLeftTimestamp(LocalDateTime leftTimestamp) {
        this.leftTimestamp = leftTimestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Utility methods
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void leaveClub() {
        this.isActive = false;
        this.leftTimestamp = LocalDateTime.now();
    }

    public void rejoinClub() {
        this.isActive = true;
        this.leftTimestamp = null;
    }

    public boolean isOwner() {
        return role == ClubRole.OWNER;
    }

    public boolean isAdmin() {
        return role == ClubRole.ADMIN || role == ClubRole.OWNER;
    }

    public boolean canManageMembers() {
        return role == ClubRole.ADMIN || role == ClubRole.OWNER;
    }

    @Override
    public String toString() {
        return "UserClub{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", clubId=" + (club != null ? club.getId() : null) +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }

    // Enum for club roles
    public enum ClubRole {
        MEMBER("Member"),
        ADMIN("Administrator"),
        OWNER("Owner");

        private final String displayName;

        ClubRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static ClubRole fromString(String role) {
            if (role == null) return MEMBER;

            switch (role.toUpperCase()) {
                case "ADMIN":
                case "ADMINISTRATOR":
                    return ADMIN;
                case "OWNER":
                    return OWNER;
                case "MEMBER":
                default:
                    return MEMBER;
            }
        }
    }
}