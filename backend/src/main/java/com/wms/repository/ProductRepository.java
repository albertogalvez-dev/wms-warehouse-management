package com.wms.repository;

import com.wms.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p WHERE " +
            "(:query IS NULL OR :query = '' OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> search(@Param("query") String query, Pageable pageable);

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);
}
