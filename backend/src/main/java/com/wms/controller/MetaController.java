package com.wms.controller;

import com.wms.entity.Carrier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meta")
@Tag(name = "Meta", description = "Metadata and reference data endpoints")
public class MetaController {

    @GetMapping("/carriers")
    @Operation(summary = "Get list of available carriers")
    public ResponseEntity<Map<String, List<String>>> getCarriers() {
        List<String> carriers = Arrays.stream(Carrier.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("carriers", carriers));
    }
}
