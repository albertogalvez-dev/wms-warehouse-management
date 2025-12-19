package com.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StartPackingRequest {

    @NotBlank(message = "Tote barcode is required")
    private String toteBarcode;

    @NotNull(message = "Station ID is required")
    private Long stationId;

    private String operator;

    // Getters and Setters
    public String getToteBarcode() {
        return toteBarcode;
    }

    public void setToteBarcode(String toteBarcode) {
        this.toteBarcode = toteBarcode;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
