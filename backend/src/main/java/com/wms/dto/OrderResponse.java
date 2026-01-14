package com.wms.dto;

import com.wms.entity.Carrier;
import com.wms.entity.Order;
import com.wms.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private String externalRef;
    private OrderStatus status;
    private Carrier carrier;
    private ShippingAddressDTO shipping;
    private List<OrderLineResponse> lines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse() {
    }

    public static OrderResponse fromEntity(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setExternalRef(order.getExternalRef());
        dto.setStatus(order.getStatus());
        dto.setCarrier(order.getCarrier());
        dto.setShipping(ShippingAddressDTO.fromEntity(order.getShipping()));
        dto.setLines(order.getLines().stream()
                .map(OrderLineResponse::fromEntity)
                .collect(Collectors.toList()));
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    public static OrderResponse fromEntity(Order order, Map<Long, ProductInventorySummary> inventoryMap) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setExternalRef(order.getExternalRef());
        dto.setStatus(order.getStatus());
        dto.setCarrier(order.getCarrier());
        dto.setShipping(ShippingAddressDTO.fromEntity(order.getShipping()));
        dto.setLines(order.getLines().stream()
                .map(line -> OrderLineResponse.fromEntity(line, inventoryMap.get(line.getProduct().getId())))
                .collect(Collectors.toList()));
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public ShippingAddressDTO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingAddressDTO shipping) {
        this.shipping = shipping;
    }

    public List<OrderLineResponse> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineResponse> lines) {
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
