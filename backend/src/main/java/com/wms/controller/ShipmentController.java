package com.wms.controller;

import com.wms.dto.ShipmentResponse;
import com.wms.dto.ShipmentSummaryResponse;
import com.wms.entity.Package;
import com.wms.entity.Shipment;
import com.wms.entity.ShipmentStatus;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.PackageRepository;
import com.wms.repository.ShipmentRepository;
import com.wms.service.PrinterClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/shipments")
@Tag(name = "Shipments", description = "Shipment and label management endpoints")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final PackageRepository packageRepository;
    private final PrinterClient printerClient;

    public ShipmentController(ShipmentRepository shipmentRepository,
            PackageRepository packageRepository,
            PrinterClient printerClient) {
        this.shipmentRepository = shipmentRepository;
        this.packageRepository = packageRepository;
        this.printerClient = printerClient;
    }

    @GetMapping
    @Operation(summary = "List shipments (paginated)")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ShipmentSummaryResponse>> listShipments(Pageable pageable) {
        Page<Shipment> shipments = shipmentRepository.findAll(pageable);
        return ResponseEntity.ok(shipments.map(ShipmentSummaryResponse::fromEntity));
    }

    @GetMapping("/{shipmentId}")
    @Operation(summary = "Get shipment details with packages")
    public ResponseEntity<ShipmentResponse> getShipment(@PathVariable Long shipmentId) {
        Shipment shipment = shipmentRepository.findByIdWithPackages(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with id: " + shipmentId));
        return ResponseEntity.ok(ShipmentResponse.fromEntity(shipment));
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Get shipment for an order (with packages)")
    public ResponseEntity<ShipmentResponse> getShipmentByOrder(@PathVariable Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderIdWithPackages(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found for order id: " + orderId));
        return ResponseEntity.ok(ShipmentResponse.fromEntity(shipment));
    }

    @GetMapping("/{shipmentId}/packages/{packageId}/label.zpl")
    @Operation(summary = "Get ZPL label for a specific package")
    public ResponseEntity<String> getPackageLabel(
            @PathVariable Long shipmentId,
            @PathVariable Long packageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Package not found with id: " + packageId));

        if (!pkg.getShipment().getId().equals(shipmentId)) {
            throw new ResourceNotFoundException(
                    "Package " + packageId + " does not belong to shipment " + shipmentId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain"));
        headers.set("Content-Disposition",
                "inline; filename=\"label-" + pkg.getTrackingCode() + ".zpl\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pkg.getLabelZpl());
    }

    @PostMapping("/{shipmentId}/print")
    @Transactional
    @Operation(summary = "Manually print all labels for a shipment (if printer enabled)")
    public ResponseEntity<ShipmentResponse> printShipment(@PathVariable Long shipmentId) {
        Shipment shipment = shipmentRepository.findByIdWithPackages(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipment not found with id: " + shipmentId));

        if (!printerClient.isEnabled()) {
            throw new InvalidOperationException("Printer is not enabled");
        }

        StringBuilder errors = new StringBuilder();
        for (Package pkg : shipment.getPackages()) {
            String printError = printerClient.printLabel(pkg.getLabelZpl());
            if (printError != null) {
                errors.append("Package ").append(pkg.getPackageNo())
                        .append(": ").append(printError).append("; ");
            } else {
                pkg.setPrintedAt(LocalDateTime.now());
                packageRepository.save(pkg);
            }
        }

        if (errors.length() > 0) {
            shipment.setPrintError(errors.toString());
            shipment.setStatus(ShipmentStatus.LABELLED);
        } else {
            shipment.setStatus(ShipmentStatus.PRINTED);
            shipment.setPrintError(null);
        }

        Shipment saved = shipmentRepository.save(shipment);
        return ResponseEntity.ok(ShipmentResponse.fromEntity(saved));
    }
}
