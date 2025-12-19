package com.wms.dto;

import com.wms.entity.PickTask;
import com.wms.entity.PickTaskStatus;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PickTaskResponse {
    private Long id;
    private Long orderId;
    private String externalRef;
    private PickTaskStatus status;
    private List<PickLineResponse> lines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PickTaskResponse() {
    }

    public static PickTaskResponse fromEntity(PickTask task) {
        PickTaskResponse dto = new PickTaskResponse();
        dto.setId(task.getId());
        dto.setOrderId(task.getOrder().getId());
        dto.setExternalRef(task.getOrder().getExternalRef());
        dto.setStatus(task.getStatus());
        dto.setLines(task.getLines().stream()
                .map(PickLineResponse::fromEntity)
                .sorted(Comparator.comparing(PickLineResponse::getLocationCode))
                .collect(Collectors.toList()));
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
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

    public PickTaskStatus getStatus() {
        return status;
    }

    public void setStatus(PickTaskStatus status) {
        this.status = status;
    }

    public List<PickLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<PickLineResponse> lines) {
        this.lines = lines;
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
