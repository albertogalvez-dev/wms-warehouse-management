package com.wms.dto;

import com.wms.entity.Stock;
import java.time.LocalDateTime;

public class StockResponse {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private Long locationId;
    private String locationCode;
    private Integer quantity;
    private LocalDateTime updatedAt;

    public StockResponse() {
    }

    public static StockResponse fromEntity(Stock stock) {
        StockResponse dto = new StockResponse();
        dto.setId(stock.getId());
        dto.setProductId(stock.getProduct().getId());
        dto.setSku(stock.getProduct().getSku());
        dto.setProductName(stock.getProduct().getName());
        dto.setLocationId(stock.getLocation().getId());
        dto.setLocationCode(stock.getLocation().getCode());
        dto.setQuantity(stock.getQuantity());
        dto.setUpdatedAt(stock.getUpdatedAt());
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
