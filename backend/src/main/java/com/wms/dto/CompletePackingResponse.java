package com.wms.dto;

public class CompletePackingResponse {

    private Long sessionId;
    private String sessionStatus;
    private String orderStatus;
    private String toteStatus;

    public CompletePackingResponse(Long sessionId, String sessionStatus, String orderStatus, String toteStatus) {
        this.sessionId = sessionId;
        this.sessionStatus = sessionStatus;
        this.orderStatus = orderStatus;
        this.toteStatus = toteStatus;
    }

    // Getters
    public Long getSessionId() {
        return sessionId;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getToteStatus() {
        return toteStatus;
    }
}
