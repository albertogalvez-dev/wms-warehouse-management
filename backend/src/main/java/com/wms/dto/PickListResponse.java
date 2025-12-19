package com.wms.dto;

import java.util.List;

public class PickListResponse {
    private Long waveId;
    private String waveCode;
    private List<LocationGroup> itemsGroupedByLocation;

    public static class LocationGroup {
        private String locationCode;
        private String sku;
        private String productName;
        private int totalQtyAssigned;
        private List<OrderBreakdown> breakdown;

        public LocationGroup(String locationCode, String sku, String productName,
                int totalQtyAssigned, List<OrderBreakdown> breakdown) {
            this.locationCode = locationCode;
            this.sku = sku;
            this.productName = productName;
            this.totalQtyAssigned = totalQtyAssigned;
            this.breakdown = breakdown;
        }

        public String getLocationCode() {
            return locationCode;
        }

        public String getSku() {
            return sku;
        }

        public String getProductName() {
            return productName;
        }

        public int getTotalQtyAssigned() {
            return totalQtyAssigned;
        }

        public List<OrderBreakdown> getBreakdown() {
            return breakdown;
        }
    }

    public static class OrderBreakdown {
        private Long orderId;
        private String toteBarcode;
        private int qtyForThatOrderAtThatLocationSku;

        public OrderBreakdown(Long orderId, String toteBarcode, int qty) {
            this.orderId = orderId;
            this.toteBarcode = toteBarcode;
            this.qtyForThatOrderAtThatLocationSku = qty;
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getToteBarcode() {
            return toteBarcode;
        }

        public int getQtyForThatOrderAtThatLocationSku() {
            return qtyForThatOrderAtThatLocationSku;
        }
    }

    public PickListResponse() {
    }

    public PickListResponse(Long waveId, String waveCode, List<LocationGroup> itemsGroupedByLocation) {
        this.waveId = waveId;
        this.waveCode = waveCode;
        this.itemsGroupedByLocation = itemsGroupedByLocation;
    }

    // Getters and Setters
    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public List<LocationGroup> getItemsGroupedByLocation() {
        return itemsGroupedByLocation;
    }

    public void setItemsGroupedByLocation(List<LocationGroup> itemsGroupedByLocation) {
        this.itemsGroupedByLocation = itemsGroupedByLocation;
    }
}
