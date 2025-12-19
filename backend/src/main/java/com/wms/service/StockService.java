package com.wms.service;

import com.wms.dto.StockAdjustRequest;
import com.wms.dto.StockResponse;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.entity.Stock;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import com.wms.repository.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    public StockService(StockRepository stockRepository,
            ProductRepository productRepository,
            LocationRepository locationRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
    }

    public StockResponse adjust(StockAdjustRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Location not found with id: " + request.getLocationId()));

        // Find existing stock or create new
        Optional<Stock> existingStock = stockRepository.findByProductAndLocation(product, location);

        Stock stock;
        if (existingStock.isPresent()) {
            stock = existingStock.get();
            int newQuantity = stock.getQuantity() + request.getDelta();

            if (newQuantity < 0) {
                throw new InvalidOperationException(
                        "Cannot adjust stock: resulting quantity would be " + newQuantity +
                                " (current: " + stock.getQuantity() + ", delta: " + request.getDelta() + ")");
            }

            stock.setQuantity(newQuantity);
        } else {
            // Creating new stock record
            if (request.getDelta() < 0) {
                throw new InvalidOperationException(
                        "Cannot create stock with negative quantity: " + request.getDelta());
            }

            stock = new Stock();
            stock.setProduct(product);
            stock.setLocation(location);
            stock.setQuantity(request.getDelta());
        }

        Stock saved = stockRepository.save(stock);
        return StockResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<StockResponse> findByFilters(Long productId, Long locationId, Pageable pageable) {
        Page<Stock> stocks;

        if (productId == null && locationId == null) {
            stocks = stockRepository.findAllWithDetails(pageable);
        } else {
            stocks = stockRepository.findByFilters(productId, locationId, pageable);
        }

        return stocks.map(StockResponse::fromEntity);
    }
}
