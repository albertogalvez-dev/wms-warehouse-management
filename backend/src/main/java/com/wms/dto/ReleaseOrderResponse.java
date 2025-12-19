package com.wms.dto;

public class ReleaseOrderResponse {
    private OrderResponse order;
    private Long pickTaskId;

    public ReleaseOrderResponse() {
    }

    public ReleaseOrderResponse(OrderResponse order, Long pickTaskId) {
        this.order = order;
        this.pickTaskId = pickTaskId;
    }

    // Getters and Setters
    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public Long getPickTaskId() {
        return pickTaskId;
    }

    public void setPickTaskId(Long pickTaskId) {
        this.pickTaskId = pickTaskId;
    }
}
