package com.wms.dto;

import com.wms.entity.Tote;
import com.wms.entity.ToteStatus;

public class ToteResponse {
    private Long id;
    private String barcode;
    private ToteStatus status;
    private Long waveId;
    private String waveCode;
    private Long orderId;
    private String orderExternalRef;
    private String carrier;
    private String shippingCity;
    private String shippingPostalCode;
    private Long packingStationId;
    private String packingStationCode;
    private PickingSummary pickingSummary;

    public static class PickingSummary {
        private int totalAssigned;
        private int totalPicked;

        public PickingSummary(int totalAssigned, int totalPicked) {
            this.totalAssigned = totalAssigned;
            this.totalPicked = totalPicked;
        }

        public int getTotalAssigned() {
            return totalAssigned;
        }

        public int getTotalPicked() {
            return totalPicked;
        }
    }

    public ToteResponse() {
    }

    public static ToteResponse fromEntity(Tote tote, int totalAssigned, int totalPicked) {
        ToteResponse dto = new ToteResponse();
        dto.setId(tote.getId());
        dto.setBarcode(tote.getBarcode());
        dto.setStatus(tote.getStatus());
        dto.setWaveId(tote.getWave().getId());
        dto.setWaveCode(tote.getWave().getCode());
        dto.setOrderId(tote.getOrder().getId());
        dto.setOrderExternalRef(tote.getOrder().getExternalRef());
        dto.setCarrier(tote.getOrder().getCarrier() != null ? tote.getOrder().getCarrier().name() : null);
        if (tote.getOrder().getShipping() != null) {
            dto.setShippingCity(tote.getOrder().getShipping().getCity());
            dto.setShippingPostalCode(tote.getOrder().getShipping().getPostalCode());
        }
        if (tote.getPackingStation() != null) {
            dto.setPackingStationId(tote.getPackingStation().getId());
            dto.setPackingStationCode(tote.getPackingStation().getCode());
        }
        dto.setPickingSummary(new PickingSummary(totalAssigned, totalPicked));
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ToteStatus getStatus() {
        return status;
    }

    public void setStatus(ToteStatus status) {
        this.status = status;
    }

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderExternalRef() {
        return orderExternalRef;
    }

    public void setOrderExternalRef(String orderExternalRef) {
        this.orderExternalRef = orderExternalRef;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public Long getPackingStationId() {
        return packingStationId;
    }

    public void setPackingStationId(Long packingStationId) {
        this.packingStationId = packingStationId;
    }

    public String getPackingStationCode() {
        return packingStationCode;
    }

    public void setPackingStationCode(String packingStationCode) {
        this.packingStationCode = packingStationCode;
    }

    public PickingSummary getPickingSummary() {
        return pickingSummary;
    }

    public void setPickingSummary(PickingSummary pickingSummary) {
        this.pickingSummary = pickingSummary;
    }
}
