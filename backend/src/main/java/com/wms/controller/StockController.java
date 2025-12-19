package com.wms.controller;

import com.wms.dto.StockAdjustRequest;
import com.wms.dto.StockResponse;
import com.wms.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock", description = "Stock management endpoints")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust stock quantity (add or subtract)")
    public ResponseEntity<StockResponse> adjust(@Valid @RequestBody StockAdjustRequest request) {
        StockResponse response = stockService.adjust(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List stock with optional filters")
    public ResponseEntity<Page<StockResponse>> list(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<StockResponse> response = stockService.findByFilters(productId, locationId, pageable);
        return ResponseEntity.ok(response);
    }
}
