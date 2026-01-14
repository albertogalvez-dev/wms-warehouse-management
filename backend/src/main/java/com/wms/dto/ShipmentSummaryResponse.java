package com.wms.dto;

import com.wms.entity.Shipment;
import java.time.LocalDateTime;

public class ShipmentSummaryResponse {
    private Long id;
    private Long orderId;
    private String externalRef;
    private String carrier;
    private String status;
    private String destinationCity;
    private String destinationCountry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShipmentSummaryResponse() {
    }

    public static ShipmentSummaryResponse fromEntity(Shipment shipment) {
        ShipmentSummaryResponse dto = new ShipmentSummaryResponse();
        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrder().getId());
        dto.setExternalRef(shipment.getOrder().getExternalRef());
        dto.setCarrier(shipment.getCarrier());
        dto.setStatus(shipment.getStatus().name());
        if (shipment.getOrder().getShipping() != null) {
            dto.setDestinationCity(shipment.getOrder().getShipping().getCity());
            dto.setDestinationCountry(shipment.getOrder().getShipping().getCountry());
        }
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
