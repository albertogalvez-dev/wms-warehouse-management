package com.wms.dto;

import com.wms.entity.Shipment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ShipmentResponse {

    public static class PackageInfo {
        private Long id;
        private int packageNo;
        private int packageCount;
        private String trackingCode;
        private String labelFormat;
        private String printedAt;

        public PackageInfo(com.wms.entity.Package pkg) {
            this.id = pkg.getId();
            this.packageNo = pkg.getPackageNo();
            this.packageCount = pkg.getPackageCount();
            this.trackingCode = pkg.getTrackingCode();
            this.labelFormat = pkg.getLabelFormat();
            this.printedAt = pkg.getPrintedAt() != null ? pkg.getPrintedAt().toString() : null;
        }

        public Long getId() {
            return id;
        }

        public int getPackageNo() {
            return packageNo;
        }

        public int getPackageCount() {
            return packageCount;
        }

        public String getTrackingCode() {
            return trackingCode;
        }

        public String getLabelFormat() {
            return labelFormat;
        }

        public String getPrintedAt() {
            return printedAt;
        }
    }

    private Long id;
    private Long orderId;
    private String externalRef;
    private String carrier;
    private String status;
    private List<PackageInfo> packages;
    private String printError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShipmentResponse fromEntity(Shipment shipment) {
        ShipmentResponse dto = new ShipmentResponse();
        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrder().getId());
        dto.setExternalRef(shipment.getOrder().getExternalRef());
        dto.setCarrier(shipment.getCarrier());
        dto.setStatus(shipment.getStatus().name());
        dto.setPackages(shipment.getPackages().stream()
                .map(PackageInfo::new)
                .collect(Collectors.toList()));
        dto.setPrintError(shipment.getPrintError());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
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

    public List<PackageInfo> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageInfo> packages) {
        this.packages = packages;
    }

    public String getPrintError() {
        return printError;
    }

    public void setPrintError(String printError) {
        this.printError = printError;
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
