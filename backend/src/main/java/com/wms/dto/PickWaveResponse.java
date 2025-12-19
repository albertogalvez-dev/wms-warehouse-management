package com.wms.dto;

import com.wms.entity.PickWave;
import com.wms.entity.PickWaveStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PickWaveResponse {
    private Long id;
    private String code;
    private PickWaveStatus status;
    private List<WaveOrderInfo> orders;
    private List<ToteInfo> totes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class WaveOrderInfo {
        private Long orderId;
        private String externalRef;
        private String status;

        public WaveOrderInfo(Long orderId, String externalRef, String status) {
            this.orderId = orderId;
            this.externalRef = externalRef;
            this.status = status;
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getExternalRef() {
            return externalRef;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class ToteInfo {
        private Long toteId;
        private String barcode;
        private Long orderId;
        private String status;
        private String packingStationCode;

        public ToteInfo(Long toteId, String barcode, Long orderId, String status, String packingStationCode) {
            this.toteId = toteId;
            this.barcode = barcode;
            this.orderId = orderId;
            this.status = status;
            this.packingStationCode = packingStationCode;
        }

        public Long getToteId() {
            return toteId;
        }

        public String getBarcode() {
            return barcode;
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getStatus() {
            return status;
        }

        public String getPackingStationCode() {
            return packingStationCode;
        }
    }

    public PickWaveResponse() {
    }

    public static PickWaveResponse fromEntity(PickWave wave) {
        PickWaveResponse dto = new PickWaveResponse();
        dto.setId(wave.getId());
        dto.setCode(wave.getCode());
        dto.setStatus(wave.getStatus());
        dto.setOrders(wave.getWaveOrders().stream()
                .map(wo -> new WaveOrderInfo(
                        wo.getOrder().getId(),
                        wo.getOrder().getExternalRef(),
                        wo.getOrder().getStatus().name()))
                .collect(Collectors.toList()));
        dto.setTotes(wave.getTotes().stream()
                .map(t -> new ToteInfo(
                        t.getId(),
                        t.getBarcode(),
                        t.getOrder().getId(),
                        t.getStatus().name(),
                        t.getPackingStation() != null ? t.getPackingStation().getCode() : null))
                .collect(Collectors.toList()));
        dto.setCreatedAt(wave.getCreatedAt());
        dto.setUpdatedAt(wave.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PickWaveStatus getStatus() {
        return status;
    }

    public void setStatus(PickWaveStatus status) {
        this.status = status;
    }

    public List<WaveOrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(List<WaveOrderInfo> orders) {
        this.orders = orders;
    }

    public List<ToteInfo> getTotes() {
        return totes;
    }

    public void setTotes(List<ToteInfo> totes) {
        this.totes = totes;
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
