package com.wms.dto;

import jakarta.validation.constraints.NotNull;

public class AssignStationRequest {

    @NotNull(message = "Station ID is required")
    private Long stationId;

    // Getters and Setters
    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
}
