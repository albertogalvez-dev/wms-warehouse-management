package com.wms.controller;

import com.wms.dto.AssignStationRequest;
import com.wms.dto.ToteResponse;
import com.wms.service.ToteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/totes")
@Tag(name = "Totes", description = "Tote management endpoints")
public class ToteController {

    private final ToteService toteService;

    public ToteController(ToteService toteService) {
        this.toteService = toteService;
    }

    @GetMapping("/{barcode}")
    @Operation(summary = "Get tote by barcode with order and picking details")
    public ResponseEntity<ToteResponse> findByBarcode(@PathVariable String barcode) {
        ToteResponse response = toteService.findByBarcode(barcode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{barcode}/assign-station")
    @Operation(summary = "Assign tote to a packing station (sets status to AT_PACKING)")
    public ResponseEntity<ToteResponse> assignStation(
            @PathVariable String barcode,
            @Valid @RequestBody AssignStationRequest request) {
        ToteResponse response = toteService.assignStation(barcode, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{barcode}/close")
    @Operation(summary = "Close tote (only if order is PICKED)")
    public ResponseEntity<ToteResponse> close(@PathVariable String barcode) {
        ToteResponse response = toteService.close(barcode);
        return ResponseEntity.ok(response);
    }
}
