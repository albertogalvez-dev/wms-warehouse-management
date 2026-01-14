package com.wms.service;

import com.wms.dto.*;
import com.wms.entity.*;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PickWaveService {

    private final PickWaveRepository pickWaveRepository;
    private final PickWaveOrderRepository pickWaveOrderRepository;
    private final OrderRepository orderRepository;
    private final ToteRepository toteRepository;
    private final PickTaskRepository pickTaskRepository;
    private final AllocationService allocationService;

    // States eligible to be added to a wave
    private static final Set<OrderStatus> WAVE_ELIGIBLE_STATES = EnumSet.of(
            OrderStatus.DRAFT, OrderStatus.RELEASED, OrderStatus.PICKING);

    public PickWaveService(PickWaveRepository pickWaveRepository,
            PickWaveOrderRepository pickWaveOrderRepository,
            OrderRepository orderRepository,
            ToteRepository toteRepository,
            PickTaskRepository pickTaskRepository,
            AllocationService allocationService) {
        this.pickWaveRepository = pickWaveRepository;
        this.pickWaveOrderRepository = pickWaveOrderRepository;
        this.orderRepository = orderRepository;
        this.toteRepository = toteRepository;
        this.pickTaskRepository = pickTaskRepository;
        this.allocationService = allocationService;
    }

    public PickWaveResponse create(PickWaveRequest request) {
        // Generate unique wave code
        String waveCode = generateWaveCode();

        // Validate orders and collect eligible ones
        List<Order> validOrders = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Long orderId : request.getOrderIds()) {
            try {
                Order order = orderRepository.findByIdWithLines(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Order not found with id: " + orderId));

                // Check if already in a wave
                if (pickWaveOrderRepository.existsByOrderId(orderId)) {
                    errors.add("Order " + orderId + " is already assigned to another wave");
                    continue;
                }

                // Check eligible status
                if (!WAVE_ELIGIBLE_STATES.contains(order.getStatus())) {
                    errors.add("Order " + orderId + " is not eligible (status: " + order.getStatus() + ")");
                    continue;
                }

                // Check shipping + carrier for DRAFT orders
                if (order.getStatus() == OrderStatus.DRAFT) {
                    if (order.getShipping() == null || order.getCarrier() == null) {
                        errors.add("Order " + orderId + " requires shipping and carrier before adding to wave");
                        continue;
                    }
                }

                validOrders.add(order);
            } catch (ResourceNotFoundException e) {
                errors.add(e.getMessage());
            }
        }

        if (validOrders.isEmpty()) {
            throw new InvalidOperationException("No valid orders to add to wave. Errors: " +
                    String.join("; ", errors));
        }

        // Create wave
        PickWave wave = new PickWave();
        wave.setCode(waveCode);
        wave.setStatus(PickWaveStatus.PLANNED);

        // Process each valid order
        int toteCounter = 1;
        List<String> orderErrors = new ArrayList<>();

        for (Order order : validOrders) {
            try {
                // If DRAFT, release (allocate stock)
                if (order.getStatus() == OrderStatus.DRAFT) {
                    PickTask pickTask = allocationService.allocateOrder(order);
                    order.setStatus(OrderStatus.RELEASED);
                    orderRepository.save(order);

                    // Check if any lines were allocated
                    if (pickTask.getLines().isEmpty()) {
                        orderErrors.add("Order " + order.getId() + " has no allocatable stock");
                        continue;
                    }
                }

                // Also check already released orders have pick tasks with lines
                var existingTask = pickTaskRepository.findByOrderId(order.getId());
                if (existingTask.isEmpty() || existingTask.get().getLines().isEmpty()) {
                    orderErrors.add("Order " + order.getId() + " has no pick lines");
                    continue;
                }

                // Add order to wave
                PickWaveOrder waveOrder = new PickWaveOrder();
                waveOrder.setOrder(order);
                wave.addWaveOrder(waveOrder);

                // Create tote
                Tote tote = new Tote();
                tote.setOrder(order);
                tote.setBarcode(generateToteBarcode(waveCode, toteCounter++));
                tote.setStatus(ToteStatus.OPEN);
                wave.addTote(tote);

            } catch (InvalidOperationException e) {
                orderErrors.add("Order " + order.getId() + ": " + e.getMessage());
            }
        }

        // Check if we have any orders after processing
        if (wave.getWaveOrders().isEmpty()) {
            throw new InvalidOperationException("No orders could be added to wave. Errors: " +
                    String.join("; ", orderErrors));
        }

        PickWave saved = pickWaveRepository.save(wave);
        return PickWaveResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PickWaveResponse findById(Long id) {
        // Load wave with orders first
        PickWave wave = pickWaveRepository.findByIdWithOrders(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickWave not found with id: " + id));
        // Then load totes to avoid MultipleBagFetchException
        pickWaveRepository.findByIdWithTotes(id);
        return PickWaveResponse.fromEntity(wave);
    }

    @Transactional(readOnly = true)
    public Page<PickWaveResponse> search(PickWaveStatus status, Pageable pageable) {
        return pickWaveRepository.findByStatusFilter(status, pageable)
                .map(PickWaveResponse::fromEntity);
    }

    public PickWaveResponse start(Long id) {
        PickWave wave = pickWaveRepository.findByIdWithOrders(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickWave not found with id: " + id));
        pickWaveRepository.findByIdWithTotes(id);

        if (wave.getStatus() != PickWaveStatus.PLANNED) {
            throw new InvalidOperationException(
                    "Cannot start wave: status is " + wave.getStatus() + ", expected PLANNED");
        }

        wave.setStatus(PickWaveStatus.IN_PROGRESS);
        PickWave saved = pickWaveRepository.save(wave);
        return PickWaveResponse.fromEntity(saved);
    }

    public PickWaveResponse complete(Long id) {
        PickWave wave = pickWaveRepository.findByIdWithOrders(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickWave not found with id: " + id));
        pickWaveRepository.findByIdWithTotes(id);

        if (wave.getStatus() != PickWaveStatus.IN_PROGRESS) {
            throw new InvalidOperationException(
                    "Cannot complete wave: status is " + wave.getStatus() + ", expected IN_PROGRESS");
        }

        // Check all orders are PICKED
        List<String> notPicked = new ArrayList<>();
        for (PickWaveOrder wo : wave.getWaveOrders()) {
            OrderStatus orderStatus = wo.getOrder().getStatus();
            if (orderStatus != OrderStatus.PICKED) {
                notPicked.add("Order " + wo.getOrder().getId() + " is " + orderStatus);
            }
        }

        if (!notPicked.isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot complete wave: not all orders are PICKED. " + String.join("; ", notPicked));
        }

        wave.setStatus(PickWaveStatus.DONE);
        PickWave saved = pickWaveRepository.save(wave);
        return PickWaveResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PickListResponse getPickList(Long id) {
        PickWave wave = pickWaveRepository.findByIdWithOrders(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickWave not found with id: " + id));
        pickWaveRepository.findByIdWithTotes(id);

        // Build a map: (locationCode, sku) -> List of (orderId, toteBarcode, qty)
        Map<String, Map<String, List<PickListResponse.OrderBreakdown>>> locationSkuMap = new TreeMap<>();
        Map<String, String> skuToProductName = new HashMap<>();
        Map<String, String> skuToImageUrl = new HashMap<>();

        for (PickWaveOrder waveOrder : wave.getWaveOrders()) {
            Order order = waveOrder.getOrder();

            // Find tote for this order
            String toteBarcode = wave.getTotes().stream()
                    .filter(t -> t.getOrder().getId().equals(order.getId()))
                    .map(Tote::getBarcode)
                    .findFirst()
                    .orElse(null);

            // Get pick lines for this order
            var pickTask = pickTaskRepository.findByIdWithDetails(
                    pickTaskRepository.findByOrderId(order.getId()).map(PickTask::getId).orElse(-1L));

            if (pickTask.isPresent()) {
                for (PickLine line : pickTask.get().getLines()) {
                    String locationCode = line.getLocation().getCode();
                    String sku = line.getProduct().getSku();

                    skuToProductName.put(sku, line.getProduct().getName());
                    skuToImageUrl.put(sku, line.getProduct().getImageUrl());

                    locationSkuMap
                            .computeIfAbsent(locationCode, k -> new TreeMap<>())
                            .computeIfAbsent(sku, k -> new ArrayList<>())
                            .add(new PickListResponse.OrderBreakdown(
                                    order.getId(), toteBarcode, line.getAssignedQty()));
                }
            }
        }

        // Convert to response format
        List<PickListResponse.LocationGroup> groups = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<PickListResponse.OrderBreakdown>>> locEntry : locationSkuMap
                .entrySet()) {
            String locationCode = locEntry.getKey();
            for (Map.Entry<String, List<PickListResponse.OrderBreakdown>> skuEntry : locEntry.getValue().entrySet()) {
                String sku = skuEntry.getKey();
                List<PickListResponse.OrderBreakdown> breakdown = skuEntry.getValue();
                int totalQty = breakdown.stream()
                        .mapToInt(PickListResponse.OrderBreakdown::getQtyForThatOrderAtThatLocationSku)
                        .sum();

                groups.add(new PickListResponse.LocationGroup(
                        locationCode, sku, skuToProductName.get(sku), skuToImageUrl.get(sku), totalQty, breakdown));
            }
        }

        return new PickListResponse(wave.getId(), wave.getCode(), groups);
    }

    private String generateWaveCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = pickWaveRepository.count() + 1;
        return String.format("WAVE-%s-%04d", dateStr, count);
    }

    private String generateToteBarcode(String waveCode, int index) {
        return String.format("TOTE-%s-%02d", waveCode.replace("WAVE-", ""), index);
    }
}
