package com.warplay.dto;

public class JoinClubRequest {
    private Long clubId;

    public JoinClubRequest() {}

    public JoinClubRequest(Long clubId) {
        this.clubId = clubId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
}