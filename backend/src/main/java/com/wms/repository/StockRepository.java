package com.wms.repository;

import com.wms.entity.Location;
import com.wms.entity.Product;
import com.wms.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductAndLocation(Product product, Location location);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.location WHERE " +
            "(:productId IS NULL OR s.product.id = :productId) AND " +
            "(:locationId IS NULL OR s.location.id = :locationId)")
    Page<Stock> findByFilters(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId,
            Pageable pageable);

    @Query("SELECT s FROM Stock s JOIN FETCH s.product JOIN FETCH s.location")
    Page<Stock> findAllWithDetails(Pageable pageable);
}
