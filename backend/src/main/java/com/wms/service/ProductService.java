package com.wms.service;

import com.wms.dto.ProductInventorySummary;
import com.wms.dto.ProductRequest;
import com.wms.dto.ProductResponse;
import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.entity.Stock;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.LocationRepository;
import com.wms.repository.ProductRepository;
import com.wms.repository.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final LocationRepository locationRepository;
    private final ProductInventoryService inventoryService;

    public ProductService(ProductRepository productRepository,
            StockRepository stockRepository,
            LocationRepository locationRepository,
            ProductInventoryService inventoryService) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.locationRepository = locationRepository;
        this.inventoryService = inventoryService;
    }

    public ProductResponse create(ProductRequest request) {
        // Check for duplicate SKU
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        // Check for duplicate barcode if provided
        if (request.getBarcode() != null && !request.getBarcode().isBlank()) {
            if (productRepository.existsByBarcode(request.getBarcode())) {
                throw new DuplicateResourceException(
                        "Product with barcode '" + request.getBarcode() + "' already exists");
            }
        }

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(
                request.getDescription() != null && !request.getDescription().isBlank()
                        ? request.getDescription()
                        : null);
        product.setBarcode(
                request.getBarcode() != null && !request.getBarcode().isBlank() ? request.getBarcode() : null);
        product.setImageUrl(
                request.getImageUrl() != null && !request.getImageUrl().isBlank()
                        ? request.getImageUrl()
                        : null);
        product.setActive(request.getActive() != null ? request.getActive() : true);

        Product saved = productRepository.save(product);
        applyStock(saved, request);
        ProductInventorySummary summary = inventoryService.loadSummary(saved.getId());
        return ProductResponse.fromEntity(saved, summary);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        ProductInventorySummary summary = inventoryService.loadSummary(product.getId());
        return ProductResponse.fromEntity(product, summary);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        Page<Product> page = productRepository.search(query, pageable);
        List<Long> productIds = page.getContent().stream().map(Product::getId).toList();
        Map<Long, ProductInventorySummary> summaryMap = inventoryService.loadSummaries(productIds);
        return page.map(product -> ProductResponse.fromEntity(product, summaryMap.get(product.getId())));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Check for duplicate SKU if changed
        if (!request.getSku().equalsIgnoreCase(product.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
            }
            product.setSku(request.getSku());
        }

        // Check for duplicate barcode if changed
        if (request.getBarcode() != null && !request.getBarcode().isBlank()) {
            if (!request.getBarcode().equals(product.getBarcode()) &&
                    productRepository.existsByBarcode(request.getBarcode())) {
                throw new DuplicateResourceException(
                        "Product with barcode '" + request.getBarcode() + "' already exists");
            }
            product.setBarcode(request.getBarcode());
        } else {
            product.setBarcode(null);
        }

        // Update allowed fields
        product.setName(request.getName());
        product.setDescription(
                request.getDescription() != null && !request.getDescription().isBlank()
                        ? request.getDescription()
                        : null);
        product.setImageUrl(
                request.getImageUrl() != null && !request.getImageUrl().isBlank()
                        ? request.getImageUrl()
                        : null);
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product saved = productRepository.save(product);
        applyStock(saved, request);
        ProductInventorySummary summary = inventoryService.loadSummary(saved.getId());
        return ProductResponse.fromEntity(saved, summary);
    }

    public void softDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    private void applyStock(Product product, ProductRequest request) {
        if (request.getLocationCode() == null && request.getStockOnHand() == null) {
            return;
        }

        String locationCode = request.getLocationCode();
        String resolvedLocation = (locationCode == null || locationCode.isBlank()) ? "A-01-01" : locationCode;

        Location location = locationRepository.findByCode(resolvedLocation)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with code: " + resolvedLocation));

        Integer stockOnHand = request.getStockOnHand();
        if (stockOnHand == null) {
            stockOnHand = 0;
        }

        Stock stock = stockRepository.findByProductAndLocation(product, location)
                .orElseGet(() -> {
                    Stock created = new Stock();
                    created.setProduct(product);
                    created.setLocation(location);
                    return created;
                });
        stock.setQuantity(stockOnHand);
        stockRepository.save(stock);
    }
}
