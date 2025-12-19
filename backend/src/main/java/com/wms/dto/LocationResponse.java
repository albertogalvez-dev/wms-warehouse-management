package com.wms.dto;

import com.wms.entity.Location;
import java.time.LocalDateTime;

public class LocationResponse {
    private Long id;
    private String code;
    private String zone;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LocationResponse() {
    }

    public static LocationResponse fromEntity(Location location) {
        LocationResponse dto = new LocationResponse();
        dto.setId(location.getId());
        dto.setCode(location.getCode());
        dto.setZone(location.getZone());
        dto.setDescription(location.getDescription());
        dto.setActive(location.getActive());
        dto.setCreatedAt(location.getCreatedAt());
        dto.setUpdatedAt(location.getUpdatedAt());
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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
