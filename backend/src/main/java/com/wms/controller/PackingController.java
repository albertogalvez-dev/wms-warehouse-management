package com.wms.controller;

import com.wms.dto.*;
import com.wms.service.PackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packing/sessions")
@Tag(name = "Packing", description = "Packing handheld workflow endpoints")
public class PackingController {

    private final PackingService packingService;

    public PackingController(PackingService packingService) {
        this.packingService = packingService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start packing session by scanning tote barcode")
    public ResponseEntity<PackingSessionResponse> startPacking(@Valid @RequestBody StartPackingRequest request) {
        PackingSessionResponse response = packingService.startPacking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{sessionId}/scan")
    @Operation(summary = "Scan product barcode or SKU to pack items")
    public ResponseEntity<PackingSessionResponse> scanProduct(
            @PathVariable Long sessionId,
            @Valid @RequestBody ScanProductRequest request) {
        PackingSessionResponse response = packingService.scanProduct(sessionId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/set-packages")
    @Operation(summary = "Define number of packages and generate shipping labels")
    public ResponseEntity<SetPackagesResponse> setPackages(
            @PathVariable Long sessionId,
            @Valid @RequestBody SetPackagesRequest request) {
        SetPackagesResponse response = packingService.setPackages(sessionId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Complete packing session (marks order as PACKED)")
    public ResponseEntity<CompletePackingResponse> completePacking(@PathVariable Long sessionId) {
        CompletePackingResponse response = packingService.completePacking(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get packing session status and details")
    public ResponseEntity<PackingSessionResponse> getSession(@PathVariable Long sessionId) {
        PackingSessionResponse response = packingService.getSession(sessionId);
        return ResponseEntity.ok(response);
    }
}
