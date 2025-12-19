package com.wms.controller;

import com.wms.dto.PickListResponse;
import com.wms.dto.PickWaveRequest;
import com.wms.dto.PickWaveResponse;
import com.wms.entity.PickWaveStatus;
import com.wms.service.PickWaveService;
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
@RequestMapping("/api/pick-waves")
@Tag(name = "Pick Waves", description = "Pick wave (batch picking) management endpoints")
public class PickWaveController {

    private final PickWaveService pickWaveService;

    public PickWaveController(PickWaveService pickWaveService) {
        this.pickWaveService = pickWaveService;
    }

    @PostMapping
    @Operation(summary = "Create a pick wave with multiple orders (creates totes, auto-releases DRAFT orders)")
    public ResponseEntity<PickWaveResponse> create(@Valid @RequestBody PickWaveRequest request) {
        PickWaveResponse response = pickWaveService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pick wave by ID with orders and totes")
    public ResponseEntity<PickWaveResponse> findById(@PathVariable Long id) {
        PickWaveResponse response = pickWaveService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search pick waves with optional status filter")
    public ResponseEntity<Page<PickWaveResponse>> search(
            @RequestParam(required = false) PickWaveStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PickWaveResponse> response = pickWaveService.search(status, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start pick wave (PLANNED -> IN_PROGRESS)")
    public ResponseEntity<PickWaveResponse> start(@PathVariable Long id) {
        PickWaveResponse response = pickWaveService.start(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete pick wave (only if all orders are PICKED)")
    public ResponseEntity<PickWaveResponse> complete(@PathVariable Long id) {
        PickWaveResponse response = pickWaveService.complete(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/pick-list")
    @Operation(summary = "Get pick list for wave (grouped by location for batch picking)")
    public ResponseEntity<PickListResponse> getPickList(@PathVariable Long id) {
        PickListResponse response = pickWaveService.getPickList(id);
        return ResponseEntity.ok(response);
    }
}
