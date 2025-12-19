package com.wms.dto;

import com.wms.entity.PickLine;
import com.wms.entity.PickLineStatus;

public class PickLineResponse {
    private Long id;
    private Long orderLineId;
    private Long productId;
    private String sku;
    private String productName;
    private Long locationId;
    private String locationCode;
    private Integer assignedQty;
    private Integer pickedQty;
    private PickLineStatus status;

    public PickLineResponse() {
    }

    public static PickLineResponse fromEntity(PickLine line) {
        PickLineResponse dto = new PickLineResponse();
        dto.setId(line.getId());
        dto.setOrderLineId(line.getOrderLine().getId());
        dto.setProductId(line.getProduct().getId());
        dto.setSku(line.getProduct().getSku());
        dto.setProductName(line.getProduct().getName());
        dto.setLocationId(line.getLocation().getId());
        dto.setLocationCode(line.getLocation().getCode());
        dto.setAssignedQty(line.getAssignedQty());
        dto.setPickedQty(line.getPickedQty());
        dto.setStatus(line.getStatus());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Long orderLineId) {
        this.orderLineId = orderLineId;
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

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Integer getAssignedQty() {
        return assignedQty;
    }

    public void setAssignedQty(Integer assignedQty) {
        this.assignedQty = assignedQty;
    }

    public Integer getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(Integer pickedQty) {
        this.pickedQty = pickedQty;
    }

    public PickLineStatus getStatus() {
        return status;
    }

    public void setStatus(PickLineStatus status) {
        this.status = status;
    }
}
