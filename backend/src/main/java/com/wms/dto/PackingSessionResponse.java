package com.wms.dto;

import java.util.List;

public class PackingSessionResponse {

    public enum Mode {
        SCAN_ITEMS,
        SET_PACKAGES,
        READY_TO_COMPLETE
    }

    public static class PackingLineInfo {
        private String sku;
        private String productName;
        private int requiredQty;
        private int packedQty;

        public PackingLineInfo(String sku, String productName, int requiredQty, int packedQty) {
            this.sku = sku;
            this.productName = productName;
            this.requiredQty = requiredQty;
            this.packedQty = packedQty;
        }

        public String getSku() {
            return sku;
        }

        public String getProductName() {
            return productName;
        }

        public int getRequiredQty() {
            return requiredQty;
        }

        public int getPackedQty() {
            return packedQty;
        }
    }

    public static class ScanResult {
        private String status; // OK or ERROR
        private String message;

        public ScanResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    private Long sessionId;
    private String toteBarcode;
    private Long orderId;
    private String externalRef;
    private String carrier;
    private Mode mode;
    private List<PackingLineInfo> lines;
    private ScanResult lastScanResult;
    private Long shipmentId;

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getToteBarcode() {
        return toteBarcode;
    }

    public void setToteBarcode(String toteBarcode) {
        this.toteBarcode = toteBarcode;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<PackingLineInfo> getLines() {
        return lines;
    }

    public void setLines(List<PackingLineInfo> lines) {
        this.lines = lines;
    }

    public ScanResult getLastScanResult() {
        return lastScanResult;
    }

    public void setLastScanResult(ScanResult lastScanResult) {
        this.lastScanResult = lastScanResult;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }
}
