package com.wms.controller;

import com.wms.dto.PackingStationRequest;
import com.wms.dto.PackingStationResponse;
import com.wms.service.PackingStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packing-stations")
@Tag(name = "Packing Stations", description = "Packing station management endpoints")
public class PackingStationController {

    private final PackingStationService packingStationService;

    public PackingStationController(PackingStationService packingStationService) {
        this.packingStationService = packingStationService;
    }

    @GetMapping
    @Operation(summary = "Get all active packing stations")
    public ResponseEntity<List<PackingStationResponse>> getAll() {
        List<PackingStationResponse> response = packingStationService.findAllActive();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new packing station")
    public ResponseEntity<PackingStationResponse> create(@Valid @RequestBody PackingStationRequest request) {
        PackingStationResponse response = packingStationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update packing station")
    public ResponseEntity<PackingStationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PackingStationRequest request) {
        PackingStationResponse response = packingStationService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
