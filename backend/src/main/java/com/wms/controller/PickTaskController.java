package com.wms.controller;

import com.wms.dto.HandheldResponse;
import com.wms.dto.PickTaskResponse;
import com.wms.entity.PickTaskStatus;
import com.wms.service.PickTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pick-tasks")
@Tag(name = "Pick Tasks", description = "Pick task management endpoints")
public class PickTaskController {

    private final PickTaskService pickTaskService;

    public PickTaskController(PickTaskService pickTaskService) {
        this.pickTaskService = pickTaskService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pick task by ID with all lines")
    public ResponseEntity<PickTaskResponse> findById(@PathVariable Long id) {
        PickTaskResponse response = pickTaskService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Search pick tasks with optional status filter")
    public ResponseEntity<Page<PickTaskResponse>> search(
            @RequestParam(required = false) PickTaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PickTaskResponse> response = pickTaskService.search(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/handheld")
    @Operation(summary = "Get handheld summary for pick task (next line to pick + progress)")
    public ResponseEntity<HandheldResponse> getHandheld(@PathVariable Long id) {
        HandheldResponse response = pickTaskService.getHandheldSummary(id);
        return ResponseEntity.ok(response);
    }
}
