package com.wms.controller;

import com.wms.dto.CompletePickingResponse;
import com.wms.dto.PickingScanRequest;
import com.wms.dto.PickingSessionResponse;
import com.wms.dto.StartPickingSessionRequest;
import com.wms.service.PickingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/picking/sessions")
@Tag(name = "Picking", description = "Picking handheld workflow endpoints")
public class PickingController {

    private final PickingService pickingService;

    public PickingController(PickingService pickingService) {
        this.pickingService = pickingService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start picking session for a wave")
    public ResponseEntity<PickingSessionResponse> start(@Valid @RequestBody StartPickingSessionRequest request) {
        PickingSessionResponse response = pickingService.start(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{sessionId}/scan")
    @Operation(summary = "Scan a code (location, product, or tote) in the picking state machine")
    public ResponseEntity<PickingSessionResponse> scan(
            @PathVariable Long sessionId,
            @Valid @RequestBody PickingScanRequest request) {
        PickingSessionResponse response = pickingService.scan(sessionId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get picking session state (resume handheld)")
    public ResponseEntity<PickingSessionResponse> getSession(@PathVariable Long sessionId) {
        PickingSessionResponse response = pickingService.getSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Complete picking session (only if wave has no OPEN pick lines)")
    public ResponseEntity<CompletePickingResponse> complete(@PathVariable Long sessionId) {
        CompletePickingResponse response = pickingService.complete(sessionId);
        return ResponseEntity.ok(response);
    }
}

