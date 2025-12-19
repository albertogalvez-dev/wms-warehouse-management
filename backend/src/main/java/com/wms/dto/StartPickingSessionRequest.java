package com.wms.dto;

import jakarta.validation.constraints.NotNull;

public class StartPickingSessionRequest {

    @NotNull(message = "Wave ID is required")
    private Long waveId;

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }
}

