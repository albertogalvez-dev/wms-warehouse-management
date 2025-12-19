package com.wms.service;

import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.entity.Stock;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import com.wms.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private StockService stockService;

    private Product testProduct;
    private Location testLocation;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("SKU-001");

        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setCode("A-01-01");

        testStock = new Stock();
        testStock.setId(1L);
        testStock.setProduct(testProduct);
        testStock.setLocation(testLocation);
        testStock.setQuantity(100);
    }

    @Test
    void findByProduct_ReturnsStockList() {
        when(stockRepository.findByProductId(1L)).thenReturn(List.of(testStock));

        List<Stock> result = stockService.findByProduct(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getQuantity());
    }

    @Test
    void findByLocation_ReturnsStockList() {
        when(stockRepository.findByLocationId(1L)).thenReturn(List.of(testStock));

        List<Stock> result = stockService.findByLocation(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAvailableQuantity_ReturnsCorrectSum() {
        when(stockRepository.findByProductId(1L)).thenReturn(List.of(testStock));

        int result = stockService.getAvailableQuantity(1L);

        assertEquals(100, result);
    }

    @Test
    void adjustStock_IncreasesQuantity() {
        when(stockRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenAnswer(inv -> inv.getArgument(0));

        Stock result = stockService.adjustStock(1L, 1L, 50);

        assertEquals(150, result.getQuantity());
        verify(stockRepository).save(testStock);
    }

    @Test
    void adjustStock_DecreasesQuantity() {
        when(stockRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenAnswer(inv -> inv.getArgument(0));

        Stock result = stockService.adjustStock(1L, 1L, -30);

        assertEquals(70, result.getQuantity());
    }

    @Test
    void adjustStock_ThrowsWhenInsufficientStock() {
        when(stockRepository.findByProductIdAndLocationId(1L, 1L)).thenReturn(Optional.of(testStock));

        assertThrows(IllegalStateException.class,
                () -> stockService.adjustStock(1L, 1L, -200));
    }
}
