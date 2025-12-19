package com.wms.service;

import com.wms.entity.*;
import com.wms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderLineRepository orderLineRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("SKU-001");
        testProduct.setName("Test Product");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setExternalRef("EXT-001");
        testOrder.setStatus(OrderStatus.DRAFT);
        testOrder.setCarrier(Carrier.DHL);
    }

    @Test
    void findById_WhenExists_ReturnsOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EXT-001", result.getExternalRef());
        verify(orderRepository).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> orderService.findById(999L));
    }

    @Test
    void findAll_ReturnsPaginatedOrders() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Order> expectedPage = new PageImpl<>(List.of(testOrder), pageRequest, 1);
        when(orderRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Order> result = orderService.findAll(pageRequest, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrder, result.getContent().get(0));
    }

    @Test
    void release_WhenDraft_UpdatesStatusToReleased() {
        testOrder.setStatus(OrderStatus.DRAFT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.release(1L);

        assertEquals(OrderStatus.RELEASED, result.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void release_WhenNotDraft_ThrowsException() {
        testOrder.setStatus(OrderStatus.RELEASED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class, () -> orderService.release(1L));
    }
}
