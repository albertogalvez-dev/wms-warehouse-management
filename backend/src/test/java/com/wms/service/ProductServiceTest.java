package com.wms.service;

import com.wms.entity.Product;
import com.wms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("SKU-001");
        testProduct.setName("Test Product");
        testProduct.setBarcode("7501234567890");
        testProduct.setWeight(new BigDecimal("1.5"));
    }

    @Test
    void findById_WhenExists_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> productService.findById(999L));
    }

    @Test
    void findBySku_WhenExists_ReturnsProduct() {
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(testProduct));

        Product result = productService.findBySku("SKU-001");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByBarcode_WhenExists_ReturnsProduct() {
        when(productRepository.findByBarcode("7501234567890")).thenReturn(Optional.of(testProduct));

        Product result = productService.findByBarcode("7501234567890");

        assertNotNull(result);
        assertEquals("7501234567890", result.getBarcode());
    }

    @Test
    void search_ReturnsMatchingProducts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageRequest, 1);
        when(productRepository.search("SKU", pageRequest)).thenReturn(expectedPage);

        Page<Product> result = productService.search("SKU", pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void create_SavesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.create(testProduct);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
        verify(productRepository).save(testProduct);
    }
}
