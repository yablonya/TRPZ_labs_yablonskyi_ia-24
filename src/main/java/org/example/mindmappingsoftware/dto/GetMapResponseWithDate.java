package org.example.mindmappingsoftware.dto;

import java.time.LocalDateTime;

public class GetMapResponseWithDate {
    private GetMapResponse mapResponse;
    private LocalDateTime savedAt;

    public GetMapResponseWithDate(GetMapResponse mapResponse, LocalDateTime savedAt) {
        this.mapResponse = mapResponse;
        this.savedAt = savedAt;
    }

    public GetMapResponse getMapResponse() {
        return mapResponse;
    }

    public void setMapResponse(GetMapResponse mapResponse) {
        this.mapResponse = mapResponse;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
