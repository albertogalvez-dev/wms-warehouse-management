package com.wms.service;

import com.wms.dto.*;
import com.wms.entity.*;
import com.wms.exception.BadRequestException;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PackingService {

    private static final long TRACKING_RANDOM_BOUND = 36L * 36 * 36 * 36 * 36 * 36; // 36^6

    private final PackingSessionRepository sessionRepository;
    private final PackingLineRepository lineRepository;
    private final ToteRepository toteRepository;
    private final PackingStationRepository stationRepository;
    private final OrderRepository orderRepository;
    private final PickTaskRepository pickTaskRepository;
    private final ProductRepository productRepository;
    private final ShipmentRepository shipmentRepository;
    private final PackageRepository packageRepository;
    private final LabelService labelService;
    private final PrinterClient printerClient;

    private final Random trackingRandom = new SecureRandom();

    public PackingService(PackingSessionRepository sessionRepository,
            PackingLineRepository lineRepository,
            ToteRepository toteRepository,
            PackingStationRepository stationRepository,
            OrderRepository orderRepository,
            PickTaskRepository pickTaskRepository,
            ProductRepository productRepository,
            ShipmentRepository shipmentRepository,
            PackageRepository packageRepository,
            LabelService labelService,
            PrinterClient printerClient) {
        this.sessionRepository = sessionRepository;
        this.lineRepository = lineRepository;
        this.toteRepository = toteRepository;
        this.stationRepository = stationRepository;
        this.orderRepository = orderRepository;
        this.pickTaskRepository = pickTaskRepository;
        this.productRepository = productRepository;
        this.shipmentRepository = shipmentRepository;
        this.packageRepository = packageRepository;
        this.labelService = labelService;
        this.printerClient = printerClient;
    }

    public PackingSessionResponse startPacking(StartPackingRequest request) {
        // Find tote
        Tote tote = toteRepository.findByBarcodeWithDetails(request.getToteBarcode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tote not found with barcode: " + request.getToteBarcode()));

        // Validate tote is AT_PACKING
        if (tote.getStatus() != ToteStatus.AT_PACKING) {
            throw new InvalidOperationException(
                    "Tote status is " + tote.getStatus() + ", expected AT_PACKING");
        }

        // Validate tote station matches request
        if (tote.getPackingStation() == null ||
                !tote.getPackingStation().getId().equals(request.getStationId())) {
            throw new InvalidOperationException(
                    "Tote is not assigned to station ID " + request.getStationId());
        }

        Order order = tote.getOrder();

        // If session already exists, return it (handheld re-scan scenario)
        Optional<PackingSession> existingSession = sessionRepository.findByToteIdAndStatus(
                tote.getId(), PackingSessionStatus.OPEN);

        if (existingSession.isPresent()) {
            PackingSession session = existingSession.get();
            if (request.getOperator() != null && !request.getOperator().isBlank() &&
                    (session.getOperator() == null || !session.getOperator().equals(request.getOperator()))) {
                session.setOperator(request.getOperator());
                sessionRepository.save(session);
            }
            return buildPackingSessionResponse(session, null);
        }

        // Validate order is PICKED (or PickTask DONE), but also allow PACKING if a previous session was cancelled
        boolean eligibleByOrderStatus = order.getStatus() == OrderStatus.PICKED ||
                order.getStatus() == OrderStatus.PACKING;

        Optional<PickTask> pickTaskOpt = pickTaskRepository.findByOrderId(order.getId());
        boolean eligibleByPickTask = pickTaskOpt.isPresent() && pickTaskOpt.get().getStatus() == PickTaskStatus.DONE;

        if (!eligibleByOrderStatus && !eligibleByPickTask) {
            throw new InvalidOperationException(
                    "Order status is " + order.getStatus() + ", expected PICKED (or PickTask DONE)");
        }

        // Create new session
        PackingStation station = stationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing station not found with id: " + request.getStationId()));

        PackingSession session = new PackingSession();
        session.setTote(tote);
        session.setStation(station);
        session.setOperator(request.getOperator());
        session.setStatus(PackingSessionStatus.OPEN);

        // Generate packing lines from pick task
        if (pickTaskOpt.isEmpty() || pickTaskOpt.get().getLines().isEmpty()) {
            throw new InvalidOperationException("Order has no pick lines");
        }

        // Aggregate by product
        Map<Long, Integer> productQtyMap = new HashMap<>();
        for (PickLine pickLine : pickTaskOpt.get().getLines()) {
            productQtyMap.merge(pickLine.getProduct().getId(),
                    pickLine.getAssignedQty(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : productQtyMap.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + entry.getKey()));

            PackingLine line = new PackingLine();
            line.setProduct(product);
            line.setRequiredQty(entry.getValue());
            line.setPackedQty(0);
            session.addLine(line);
        }

        // Update order status to PACKING (only if not already)
        if (order.getStatus() != OrderStatus.PACKING) {
            order.setStatus(OrderStatus.PACKING);
            orderRepository.save(order);
        }

        PackingSession saved = sessionRepository.save(session);
        return buildPackingSessionResponse(saved, null);
    }

    public PackingSessionResponse scanProduct(Long sessionId, ScanProductRequest request) {
        PackingSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing session not found with id: " + sessionId));

        if (session.getStatus() != PackingSessionStatus.OPEN) {
            throw new InvalidOperationException(
                    "Session status is " + session.getStatus() + ", expected OPEN");
        }

        // Find product by barcode or SKU
        String code = request.getCode().trim();

        Optional<Product> productOpt = productRepository.findByBarcode(code);
        if (productOpt.isEmpty()) {
            productOpt = productRepository.findBySku(code);
        }

        if (productOpt.isEmpty()) {
            throw new BadRequestException("Unknown product code: " + code);
        }

        Product product = productOpt.get();

        // Find packing line for this product
        Optional<PackingLine> lineOpt = session.getLines().stream()
                .filter(l -> l.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (lineOpt.isEmpty()) {
            throw new InvalidOperationException(
                    "Product not expected for this order: " + product.getSku());
        }

        PackingLine line = lineOpt.get();

        // Check if already complete
        if (line.getPackedQty() >= line.getRequiredQty()) {
            throw new InvalidOperationException(
                    "Quantity already complete for " + product.getSku());
        }

        int qtyToAdd = request.getQty();
        int remaining = line.getRequiredQty() - line.getPackedQty();
        if (qtyToAdd > remaining) {
            throw new InvalidOperationException(
                    "Scan would exceed required quantity for " + product.getSku() + " (remaining: " + remaining + ")");
        }

        int newPackedQty = line.getPackedQty() + qtyToAdd;
        line.setPackedQty(newPackedQty);
        lineRepository.save(line);

        String message = String.format("Scanned %s: %d/%d",
                product.getSku(), newPackedQty, line.getRequiredQty());

        return buildPackingSessionResponse(session,
                new PackingSessionResponse.ScanResult("OK", message));
    }

    public SetPackagesResponse setPackages(Long sessionId, SetPackagesRequest request) {
        PackingSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing session not found with id: " + sessionId));

        if (session.getStatus() != PackingSessionStatus.OPEN) {
            throw new InvalidOperationException(
                    "Session status is " + session.getStatus() + ", expected OPEN");
        }

        // Validate all lines complete
        for (PackingLine line : session.getLines()) {
            if (line.getPackedQty() < line.getRequiredQty()) {
                throw new InvalidOperationException(
                        "Not all items packed. " + line.getProduct().getSku() + " is " +
                                line.getPackedQty() + "/" + line.getRequiredQty());
            }
        }

        Order order = session.getTote().getOrder();

        Shipment shipment = shipmentRepository.findByOrderIdWithPackages(order.getId())
                .orElseGet(() -> {
                    Shipment s = new Shipment();
                    s.setOrder(order);
                    s.setCarrier(order.getCarrier().name());
                    s.setStatus(ShipmentStatus.CREATED);
                    return s;
                });

        // Idempotency: if packages already exist, only allow returning them with the same packageCount
        if (!shipment.getPackages().isEmpty()) {
            List<com.wms.entity.Package> existing = new ArrayList<>(shipment.getPackages());
            existing.sort(Comparator.comparing(com.wms.entity.Package::getPackageNo));

            Integer existingPackageCount = existing.get(0).getPackageCount();
            if (!Objects.equals(existingPackageCount, request.getPackageCount()) ||
                    existing.size() != request.getPackageCount()) {
                throw new InvalidOperationException(
                        "Packages already created for this order (existing packageCount=" + existingPackageCount +
                                ", requested=" + request.getPackageCount() + ")");
            }

            List<SetPackagesResponse.PackageInfo> packageInfos = existing.stream()
                    .map(p -> new SetPackagesResponse.PackageInfo(
                            p.getId(),
                            p.getPackageNo(),
                            p.getTrackingCode(),
                            p.getLabelFormat(),
                            p.getLabelZpl()))
                    .collect(Collectors.toList());

            return new SetPackagesResponse(shipment.getId(), packageInfos);
        }

        // Create packages
        for (int i = 1; i <= request.getPackageCount(); i++) {
            com.wms.entity.Package pkg = new com.wms.entity.Package();
            pkg.setPackageNo(i);
            pkg.setPackageCount(request.getPackageCount());
            pkg.setTrackingCode(generateUniqueTrackingCode(order.getCarrier().name(), order.getId(), i));

            // Generate ZPL label
            String orderRef = order.getExternalRef() != null && !order.getExternalRef().isBlank()
                    ? order.getExternalRef()
                    : String.valueOf(order.getId());
            String zpl = labelService.generateZPL(
                    order.getCarrier().name(),
                    orderRef,
                    order.getShipping(),
                    pkg.getTrackingCode(),
                    i,
                    request.getPackageCount());
            pkg.setLabelZpl(zpl);
            pkg.setLabelFormat("ZPL");

            shipment.addPackage(pkg);
        }

        // Update shipment status
        shipment.setStatus(ShipmentStatus.LABELLED);

        // Try to print if enabled
        if (printerClient.isEnabled()) {
            StringBuilder errors = new StringBuilder();
            for (com.wms.entity.Package pkg : shipment.getPackages()) {
                String printError = printerClient.printLabel(pkg.getLabelZpl());
                if (printError != null) {
                    errors.append("Package ").append(pkg.getPackageNo())
                            .append(": ").append(printError).append("; ");
                } else {
                    pkg.setPrintedAt(LocalDateTime.now());
                }
            }

            if (errors.length() > 0) {
                shipment.setPrintError(errors.toString());
            } else {
                shipment.setStatus(ShipmentStatus.PRINTED);
            }
        }

        Shipment saved = shipmentRepository.save(shipment);

        List<com.wms.entity.Package> savedPackages = new ArrayList<>(saved.getPackages());
        savedPackages.sort(Comparator.comparing(com.wms.entity.Package::getPackageNo));

        List<SetPackagesResponse.PackageInfo> packageInfos = savedPackages.stream()
                .map(p -> new SetPackagesResponse.PackageInfo(
                        p.getId(),
                        p.getPackageNo(),
                        p.getTrackingCode(),
                        p.getLabelFormat(),
                        p.getLabelZpl()))
                .collect(Collectors.toList());

        return new SetPackagesResponse(saved.getId(), packageInfos);
    }

    public CompletePackingResponse completePacking(Long sessionId) {
        PackingSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing session not found with id: " + sessionId));

        if (session.getStatus() != PackingSessionStatus.OPEN) {
            throw new InvalidOperationException(
                    "Session status is " + session.getStatus() + ", expected OPEN");
        }

        // Validate lines complete
        for (PackingLine line : session.getLines()) {
            if (line.getPackedQty() < line.getRequiredQty()) {
                throw new InvalidOperationException("Not all items packed");
            }
        }

        Order order = session.getTote().getOrder();

        // Validate shipment created
        Optional<Shipment> shipment = shipmentRepository.findByOrderId(order.getId());
        if (shipment.isEmpty() || shipment.get().getPackages().isEmpty()) {
            throw new InvalidOperationException("No packages created");
        }

        // Update statuses
        order.setStatus(OrderStatus.PACKED);
        session.getTote().setStatus(ToteStatus.CLOSED);
        session.setStatus(PackingSessionStatus.DONE);
        session.setFinishedAt(LocalDateTime.now());

        orderRepository.save(order);
        toteRepository.save(session.getTote());
        sessionRepository.save(session);

        return new CompletePackingResponse(
                session.getId(),
                session.getStatus().name(),
                order.getStatus().name(),
                session.getTote().getStatus().name());
    }

    @Transactional(readOnly = true)
    public PackingSessionResponse getSession(Long sessionId) {
        PackingSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing session not found with id: " + sessionId));
        return buildPackingSessionResponse(session, null);
    }

    private PackingSessionResponse buildPackingSessionResponse(PackingSession session,
            PackingSessionResponse.ScanResult scanResult) {
        PackingSessionResponse response = new PackingSessionResponse();
        response.setSessionId(session.getId());
        response.setToteBarcode(session.getTote().getBarcode());
        response.setOrderId(session.getTote().getOrder().getId());
        response.setExternalRef(session.getTote().getOrder().getExternalRef());
        response.setCarrier(session.getTote().getOrder().getCarrier().name());

        List<PackingSessionResponse.PackingLineInfo> lines = session.getLines().stream()
                .map(l -> new PackingSessionResponse.PackingLineInfo(
                        l.getProduct().getSku(),
                        l.getProduct().getName(),
                        l.getRequiredQty(),
                        l.getPackedQty()))
                .collect(Collectors.toList());
        response.setLines(lines);

        // Determine mode
        boolean allComplete = lines.stream()
                .allMatch(l -> l.getPackedQty() >= l.getRequiredQty());

        Optional<Shipment> shipment = shipmentRepository.findByOrderId(
                session.getTote().getOrder().getId());

        if (shipment.isPresent() && !shipment.get().getPackages().isEmpty()) {
            response.setMode(PackingSessionResponse.Mode.READY_TO_COMPLETE);
            response.setShipmentId(shipment.get().getId());
        } else if (allComplete) {
            response.setMode(PackingSessionResponse.Mode.SET_PACKAGES);
        } else {
            response.setMode(PackingSessionResponse.Mode.SCAN_ITEMS);
        }

        response.setLastScanResult(scanResult);
        return response;
    }

    private String generateTrackingCode(String carrier, Long orderId, int packageNo) {
        // Mock tracking: {carrier}-{orderId}-{packageNo}-{randomShort}
        long value = Math.floorMod(trackingRandom.nextLong(), TRACKING_RANDOM_BOUND);
        String randomShort = Long.toString(value, 36).toUpperCase(Locale.ROOT);
        if (randomShort.length() < 6) {
            randomShort = "000000".substring(randomShort.length()) + randomShort;
        } else if (randomShort.length() > 6) {
            randomShort = randomShort.substring(0, 6);
        }
        return String.format("%s-%d-%02d-%s", carrier, orderId, packageNo, randomShort);
    }

    private String generateUniqueTrackingCode(String carrier, Long orderId, int packageNo) {
        for (int attempt = 0; attempt < 20; attempt++) {
            String trackingCode = generateTrackingCode(carrier, orderId, packageNo);
            if (!packageRepository.existsByTrackingCode(trackingCode)) {
                return trackingCode;
            }
        }
        throw new IllegalStateException("Could not generate unique tracking code");
    }
}
