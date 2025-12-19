package com.wms.dto;

import com.wms.entity.OrderLine;

public class OrderLineResponse {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private Integer requestedQty;
    private Integer allocatedQty;
    private Integer pickedQty;

    public OrderLineResponse() {
    }

    public static OrderLineResponse fromEntity(OrderLine line) {
        OrderLineResponse dto = new OrderLineResponse();
        dto.setId(line.getId());
        dto.setProductId(line.getProduct().getId());
        dto.setSku(line.getProduct().getSku());
        dto.setProductName(line.getProduct().getName());
        dto.setRequestedQty(line.getRequestedQty());
        dto.setAllocatedQty(line.getAllocatedQty());
        dto.setPickedQty(line.getPickedQty());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Integer getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Integer allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public Integer getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(Integer pickedQty) {
        this.pickedQty = pickedQty;
    }
}
