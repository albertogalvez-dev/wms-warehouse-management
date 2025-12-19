package com.wms.dto;

import com.wms.entity.PackingStation;
import java.time.LocalDateTime;

public class PackingStationResponse {
    private Long id;
    private String code;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PackingStationResponse() {
    }

    public static PackingStationResponse fromEntity(PackingStation station) {
        PackingStationResponse dto = new PackingStationResponse();
        dto.setId(station.getId());
        dto.setCode(station.getCode());
        dto.setName(station.getName());
        dto.setActive(station.getActive());
        dto.setCreatedAt(station.getCreatedAt());
        dto.setUpdatedAt(station.getUpdatedAt());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
