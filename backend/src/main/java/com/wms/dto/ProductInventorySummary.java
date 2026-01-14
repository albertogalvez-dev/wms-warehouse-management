package com.wms.dto;

public class ProductInventorySummary {
    private String locationCode;
    private Integer stockOnHand;
    private Integer stockAllocated;
    private Integer stockAvailable;

    public ProductInventorySummary() {
    }

    public ProductInventorySummary(String locationCode, Integer stockOnHand, Integer stockAllocated) {
        this.locationCode = locationCode;
        this.stockOnHand = stockOnHand != null ? stockOnHand : 0;
        this.stockAllocated = stockAllocated != null ? stockAllocated : 0;
        int available = this.stockOnHand - this.stockAllocated;
        this.stockAvailable = Math.max(available, 0);
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Integer getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(Integer stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public Integer getStockAllocated() {
        return stockAllocated;
    }

    public void setStockAllocated(Integer stockAllocated) {
        this.stockAllocated = stockAllocated;
    }

    public Integer getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(Integer stockAvailable) {
        this.stockAvailable = stockAvailable;
    }
}
