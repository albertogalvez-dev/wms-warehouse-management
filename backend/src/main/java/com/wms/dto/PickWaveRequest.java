package com.wms.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PickWaveRequest {

    @NotEmpty(message = "Wave must have at least one order")
    private List<Long> orderIds;

    // Getters and Setters
    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
