package com.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderLineRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Requested quantity is required")
    @Min(value = 1, message = "Requested quantity must be at least 1")
    private Integer requestedQty;

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }
}
