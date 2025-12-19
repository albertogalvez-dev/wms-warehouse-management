package com.wms.service;

import com.wms.dto.ProductRequest;
import com.wms.dto.ProductResponse;
import com.wms.entity.Product;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        product.setBarcode(
                request.getBarcode() != null && !request.getBarcode().isBlank() ? request.getBarcode() : null);
        product.setActive(request.getActive() != null ? request.getActive() : true);

        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        return productRepository.search(query, pageable)
                .map(ProductResponse::fromEntity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

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

        // Update allowed fields (SKU is immutable)
        product.setName(request.getName());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }

    public void softDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }
}
