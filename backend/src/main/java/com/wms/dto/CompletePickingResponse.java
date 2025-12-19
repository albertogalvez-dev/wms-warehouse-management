package com.wms.dto;

public class CompletePickingResponse {

    private Long sessionId;
    private String sessionStatus;
    private String waveStatus;

    public CompletePickingResponse(Long sessionId, String sessionStatus, String waveStatus) {
        this.sessionId = sessionId;
        this.sessionStatus = sessionStatus;
        this.waveStatus = waveStatus;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public String getWaveStatus() {
        return waveStatus;
    }
}

