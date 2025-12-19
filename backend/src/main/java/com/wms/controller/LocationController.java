package com.wms.controller;

import com.wms.dto.LocationRequest;
import com.wms.dto.LocationResponse;
import com.wms.service.LocationService;
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
@RequestMapping("/api/locations")
@Tag(name = "Locations", description = "Location management endpoints")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @Operation(summary = "Create a new location")
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<LocationResponse> findById(@PathVariable Long id) {
        LocationResponse response = locationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search locations with pagination")
    public ResponseEntity<Page<LocationResponse>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LocationResponse> response = locationService.search(query, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update location by ID")
    public ResponseEntity<LocationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete location (sets active=false)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
