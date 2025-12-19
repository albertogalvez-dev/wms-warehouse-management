package com.wms.controller;

import com.wms.dto.OrderRequest;
import com.wms.dto.OrderResponse;
import com.wms.dto.ReleaseOrderResponse;
import com.wms.dto.ShippingUpdateRequest;
import com.wms.entity.OrderStatus;
import com.wms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order with lines")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID with lines")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        OrderResponse response = orderService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search orders with optional status filter")
    public ResponseEntity<Page<OrderResponse>> search(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> response = orderService.search(status, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Release order to picking (allocate stock and create pick task)")
    public ResponseEntity<ReleaseOrderResponse> release(@PathVariable Long id) {
        ReleaseOrderResponse response = orderService.release(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/shipping")
    @Operation(summary = "Update shipping address and carrier (only in DRAFT or RELEASED state)")
    public ResponseEntity<OrderResponse> updateShipping(
            @PathVariable Long id,
            @Valid @RequestBody ShippingUpdateRequest request) {
        OrderResponse response = orderService.updateShipping(id, request);
        return ResponseEntity.ok(response);
    }
}
