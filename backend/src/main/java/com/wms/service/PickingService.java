package com.wms.service;

import com.wms.dto.CompletePickingResponse;
import com.wms.dto.PickingScanRequest;
import com.wms.dto.PickingSessionResponse;
import com.wms.dto.StartPickingSessionRequest;
import com.wms.entity.*;
import com.wms.exception.BadRequestException;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PickingService {

    private final PickingSessionRepository pickingSessionRepository;
    private final PickingAuditService pickingAuditService;

    private final PickWaveRepository pickWaveRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final ToteRepository toteRepository;
    private final PickLineRepository pickLineRepository;
    private final PickTaskRepository pickTaskRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public PickingService(PickingSessionRepository pickingSessionRepository,
            PickingAuditService pickingAuditService,
            PickWaveRepository pickWaveRepository,
            LocationRepository locationRepository,
            ProductRepository productRepository,
            ToteRepository toteRepository,
            PickLineRepository pickLineRepository,
            PickTaskRepository pickTaskRepository,
            OrderRepository orderRepository,
            OrderLineRepository orderLineRepository) {
        this.pickingSessionRepository = pickingSessionRepository;
        this.pickingAuditService = pickingAuditService;
        this.pickWaveRepository = pickWaveRepository;
        this.locationRepository = locationRepository;
        this.productRepository = productRepository;
        this.toteRepository = toteRepository;
        this.pickLineRepository = pickLineRepository;
        this.pickTaskRepository = pickTaskRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
    }

    public PickingSessionResponse start(StartPickingSessionRequest request) {
        PickWave wave = pickWaveRepository.findByIdWithOrders(request.getWaveId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PickWave not found with id: " + request.getWaveId()));

        if (wave.getStatus() == PickWaveStatus.DONE) {
            throw new InvalidOperationException("Wave is DONE");
        }
        if (wave.getStatus() == PickWaveStatus.CANCELLED) {
            throw new InvalidOperationException("Wave is CANCELLED");
        }

        // If PLANNED -> IN_PROGRESS
        if (wave.getStatus() == PickWaveStatus.PLANNED) {
            wave.setStatus(PickWaveStatus.IN_PROGRESS);
            pickWaveRepository.save(wave);
        }

        // Idempotent: reuse existing OPEN session for the wave
        PickingSession session = pickingSessionRepository.findByWaveIdAndStatus(wave.getId(), PickingSessionStatus.OPEN)
                .orElseGet(() -> {
                    PickingSession ps = new PickingSession();
                    ps.setWave(wave);
                    ps.setStatus(PickingSessionStatus.OPEN);
                    return pickingSessionRepository.save(ps);
                });

        return buildResponse(session, null);
    }

    public PickingSessionResponse scan(Long sessionId, PickingScanRequest request) {
        PickingSession session = pickingSessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Picking session not found with id: " + sessionId));

        if (session.getStatus() != PickingSessionStatus.OPEN) {
            throw new InvalidOperationException(
                    "Session status is " + session.getStatus() + ", expected OPEN");
        }

        PickWave wave = session.getWave();
        if (wave.getStatus() == PickWaveStatus.DONE) {
            pickingAuditService.log(session.getId(), request.getCode(), PickingScanEventType.ERROR, "Wave is DONE");
            throw new InvalidOperationException("Wave is DONE");
        }
        if (wave.getStatus() == PickWaveStatus.CANCELLED) {
            pickingAuditService.log(session.getId(), request.getCode(), PickingScanEventType.ERROR, "Wave is CANCELLED");
            throw new InvalidOperationException("Wave is CANCELLED");
        }

        String code = request.getCode().trim();
        int qty = request.getQty();

        // 1) Location?
        var locationOpt = locationRepository.findByCode(code);
        if (locationOpt.isPresent()) {
            Location location = locationOpt.get();
            session.setCurrentLocation(location);
            session.setCurrentProduct(null);
            pickingSessionRepository.save(session);
            pickingAuditService.log(session.getId(), code, PickingScanEventType.LOCATION,
                    "Location set to " + location.getCode());

            return buildResponse(session, new PickingSessionResponse.ScanResult("OK",
                    "Location: " + location.getCode()));
        }

        // 2) Product?
        var productOpt = productRepository.findByBarcode(code);
        if (productOpt.isEmpty()) {
            productOpt = productRepository.findBySku(code);
        }

        if (productOpt.isPresent()) {
            if (session.getCurrentLocation() == null) {
                pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR, "Scan a location first");
                throw new InvalidOperationException("Scan a location first");
            }

            Product product = productOpt.get();
            session.setCurrentProduct(product);
            pickingSessionRepository.save(session);
            pickingAuditService.log(session.getId(), code, PickingScanEventType.PRODUCT,
                    "Product set to " + product.getSku());

            return buildResponse(session, new PickingSessionResponse.ScanResult("OK",
                    "Product: " + product.getSku()));
        }

        // 3) Tote?
        var toteOpt = toteRepository.findByBarcodeWithDetails(code);
        if (toteOpt.isPresent()) {
            if (session.getCurrentLocation() == null) {
                pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR, "Scan a location first");
                throw new InvalidOperationException("Scan a location first");
            }
            if (session.getCurrentProduct() == null) {
                pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR, "Scan a product first");
                throw new InvalidOperationException("Scan a product first");
            }

            Tote tote = toteOpt.get();
            if (!Objects.equals(tote.getWave().getId(), wave.getId())) {
                pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR,
                        "Tote does not belong to wave " + wave.getId());
                throw new InvalidOperationException("Tote does not belong to this wave");
            }

            Order order = tote.getOrder();
            Location location = session.getCurrentLocation();
            Product product = session.getCurrentProduct();

            PickLine line = pickLineRepository
                    .findFirstByPickTaskOrderIdAndLocationIdAndProductIdAndStatusOrderByIdAsc(
                            order.getId(),
                            location.getId(),
                            product.getId(),
                            PickLineStatus.OPEN)
                    .orElseThrow(() -> {
                        pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR,
                                "Nothing to pick for tote at this location/product");
                        return new InvalidOperationException("Nothing to pick for this tote at this location/product");
                    });

            int remaining = line.getAssignedQty() - line.getPickedQty();
            if (qty > remaining) {
                pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR,
                        "Cannot pick more than assigned (remaining: " + remaining + ")");
                throw new InvalidOperationException(
                        "Cannot pick more than assigned for this line (remaining: " + remaining + ")");
            }

            // Mark task/order as in progress when first picking happens
            PickTask pickTask = line.getPickTask();
            if (pickTask.getStatus() == PickTaskStatus.OPEN) {
                pickTask.setStatus(PickTaskStatus.IN_PROGRESS);
                pickTaskRepository.save(pickTask);
            }
            if (order.getStatus() == OrderStatus.RELEASED) {
                order.setStatus(OrderStatus.PICKING);
                orderRepository.save(order);
            }

            // Apply picking
            line.setPickedQty(line.getPickedQty() + qty);
            line.getOrderLine().setPickedQty(line.getOrderLine().getPickedQty() + qty);

            if (Objects.equals(line.getPickedQty(), line.getAssignedQty())) {
                line.setStatus(PickLineStatus.DONE);
            }

            pickLineRepository.save(line);
            orderLineRepository.save(line.getOrderLine());

            // If all lines DONE -> picktask DONE + order PICKED
            if (!pickLineRepository.existsByPickTaskIdAndStatus(pickTask.getId(), PickLineStatus.OPEN)) {
                pickTask.setStatus(PickTaskStatus.DONE);
                pickTaskRepository.save(pickTask);

                order.setStatus(OrderStatus.PICKED);
                orderRepository.save(order);
            }

            pickingAuditService.log(session.getId(), code, PickingScanEventType.TOTE,
                    "Picked " + qty + " of " + product.getSku() + " into " + tote.getBarcode());

            // If there is nothing else to pick for this (location, product) combo in the wave,
            // clear current product to guide the operator.
            var candidatesAfter = findCandidates(wave.getId(), location.getId(), product.getId());
            if (candidatesAfter.isEmpty()) {
                session.setCurrentProduct(null);
                pickingSessionRepository.save(session);
            }

            return buildResponse(session, new PickingSessionResponse.ScanResult("OK",
                    "Picked into " + tote.getBarcode() + " (" + qty + "x " + product.getSku() + ")"));
        }

        pickingAuditService.log(session.getId(), code, PickingScanEventType.ERROR, "Unknown scan code");
        throw new BadRequestException("Unknown scan code: " + code);
    }

    @Transactional(readOnly = true)
    public PickingSessionResponse getSession(Long sessionId) {
        PickingSession session = pickingSessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Picking session not found with id: " + sessionId));
        return buildResponse(session, null);
    }

    public CompletePickingResponse complete(Long sessionId) {
        PickingSession session = pickingSessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Picking session not found with id: " + sessionId));

        if (session.getStatus() != PickingSessionStatus.OPEN) {
            throw new InvalidOperationException(
                    "Session status is " + session.getStatus() + ", expected OPEN");
        }

        PickWave wave = session.getWave();
        if (wave.getStatus() == PickWaveStatus.DONE) {
            throw new InvalidOperationException("Wave is DONE");
        }
        if (wave.getStatus() == PickWaveStatus.CANCELLED) {
            throw new InvalidOperationException("Wave is CANCELLED");
        }

        long openLines = pickLineRepository.countPickLinesByWaveAndStatus(wave.getId(), PickLineStatus.OPEN);
        if (openLines > 0) {
            throw new InvalidOperationException("Cannot complete session: wave still has OPEN pick lines");
        }

        session.setStatus(PickingSessionStatus.DONE);
        pickingSessionRepository.save(session);

        wave.setStatus(PickWaveStatus.DONE);
        pickWaveRepository.save(wave);

        return new CompletePickingResponse(session.getId(), session.getStatus().name(), wave.getStatus().name());
    }

    private PickingSessionResponse buildResponse(PickingSession session, PickingSessionResponse.ScanResult scanResult) {
        PickingSessionResponse response = new PickingSessionResponse();
        response.setSessionId(session.getId());
        response.setWaveId(session.getWave().getId());
        response.setStatus(session.getStatus().name());

        if (session.getCurrentLocation() != null) {
            response.setCurrentLocationCode(session.getCurrentLocation().getCode());
        }
        if (session.getCurrentProduct() != null) {
            response.setCurrentSku(session.getCurrentProduct().getSku());
        }

        PickingSessionResponse.Mode mode;
        if (session.getCurrentLocation() == null) {
            mode = PickingSessionResponse.Mode.EXPECT_LOCATION;
        } else if (session.getCurrentProduct() == null) {
            mode = PickingSessionResponse.Mode.EXPECT_PRODUCT;
        } else {
            mode = PickingSessionResponse.Mode.EXPECT_TOTE;
        }
        response.setMode(mode);

        // Hints and candidates
        response.setNextLocations(
                pickLineRepository.findNextPendingLocations(session.getWave().getId(), PageRequest.of(0, 5)));

        if (mode == PickingSessionResponse.Mode.EXPECT_TOTE) {
            List<PickingSessionResponse.Candidate> candidates = findCandidates(
                    session.getWave().getId(),
                    session.getCurrentLocation().getId(),
                    session.getCurrentProduct().getId());
            response.setCandidates(candidates);
        }

        int remainingLines = (int) pickLineRepository.countPickLinesByWaveAndStatus(
                session.getWave().getId(), PickLineStatus.OPEN);
        int doneLines = (int) pickLineRepository.countPickLinesByWaveAndStatus(
                session.getWave().getId(), PickLineStatus.DONE);
        response.setProgress(new PickingSessionResponse.Progress(remainingLines, doneLines));

        response.setLastScanResult(scanResult);
        return response;
    }

    private List<PickingSessionResponse.Candidate> findCandidates(Long waveId, Long locationId, Long productId) {
        return pickLineRepository.findCandidateTotesForWaveLocationProduct(waveId, locationId, productId).stream()
                .filter(r -> r.getRemainingQty() != null && r.getRemainingQty() > 0)
                .sorted(Comparator.comparing(PickLineRepository.ToteCandidateRow::getToteBarcode))
                .map(r -> new PickingSessionResponse.Candidate(
                        r.getToteBarcode(),
                        r.getOrderId(),
                        r.getExternalRef(),
                        r.getRemainingQty().intValue()))
                .collect(Collectors.toList());
    }
}

