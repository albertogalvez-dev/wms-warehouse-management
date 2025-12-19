package com.wms.service;

import com.wms.dto.*;
import com.wms.entity.*;
import com.wms.exception.BadRequestException;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.OrderRepository;
import com.wms.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AllocationService allocationService;

    // States that allow shipping modification
    private static final Set<OrderStatus> SHIPPING_EDITABLE_STATES = EnumSet.of(
            OrderStatus.DRAFT, OrderStatus.RELEASED);

    public OrderService(OrderRepository orderRepository,
            ProductRepository productRepository,
            AllocationService allocationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.allocationService = allocationService;
    }

    public OrderResponse create(OrderRequest request) {
        // Check for duplicate external ref
        if (request.getExternalRef() != null && !request.getExternalRef().isBlank()) {
            if (orderRepository.existsByExternalRef(request.getExternalRef())) {
                throw new DuplicateResourceException(
                        "Order with external reference '" + request.getExternalRef() + "' already exists");
            }
        }

        // Check for duplicate products in lines
        Set<Long> productIds = new HashSet<>();
        for (OrderLineRequest lineReq : request.getLines()) {
            if (!productIds.add(lineReq.getProductId())) {
                throw new BadRequestException(
                        "Duplicate product ID " + lineReq.getProductId() + " in order lines");
            }
        }

        Order order = new Order();
        order.setExternalRef(request.getExternalRef() != null && !request.getExternalRef().isBlank()
                ? request.getExternalRef()
                : null);
        order.setStatus(OrderStatus.DRAFT);
        order.setCarrier(request.getCarrier());
        order.setShipping(request.getShipping().toEntity());

        for (OrderLineRequest lineReq : request.getLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + lineReq.getProductId()));

            OrderLine line = new OrderLine();
            line.setProduct(product);
            line.setRequestedQty(lineReq.getRequestedQty());
            order.addLine(line);
        }

        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findByIdWithLines(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> search(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusFilter(status, pageable)
                .map(OrderResponse::fromEntity);
    }

    public ReleaseOrderResponse release(Long orderId) {
        Order order = orderRepository.findByIdWithLines(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new InvalidOperationException(
                    "Cannot release order: status is " + order.getStatus() + ", expected DRAFT");
        }

        PickTask pickTask = allocationService.allocateOrder(order);

        order.setStatus(OrderStatus.RELEASED);
        orderRepository.save(order);

        return new ReleaseOrderResponse(OrderResponse.fromEntity(order), pickTask.getId());
    }

    public OrderResponse updateShipping(Long orderId, ShippingUpdateRequest request) {
        Order order = orderRepository.findByIdWithLines(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!SHIPPING_EDITABLE_STATES.contains(order.getStatus())) {
            throw new InvalidOperationException(
                    "Cannot update shipping: order status is " + order.getStatus() +
                            ". Shipping can only be modified in DRAFT or RELEASED state.");
        }

        order.setCarrier(request.getCarrier());
        order.setShipping(request.getShipping().toEntity());

        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }
}
