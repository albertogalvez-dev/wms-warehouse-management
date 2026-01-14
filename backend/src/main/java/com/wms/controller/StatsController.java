package com.wms.controller;

import com.wms.dto.OrderStatsResponse;
import com.wms.dto.WorkerStatsResponse;
import com.wms.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Stats", description = "Operational KPI and worker productivity stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/orders")
    @Operation(summary = "Order KPIs for a date range (defaults to last 7 days)")
    public ResponseEntity<OrderStatsResponse> getOrderStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(statsService.getOrderStats(from, to));
    }

    @GetMapping("/workers")
    @Operation(summary = "Worker productivity stats for a date range (defaults to last 7 days)")
    public ResponseEntity<WorkerStatsResponse> getWorkerStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(statsService.getWorkerStats(from, to));
    }
}
