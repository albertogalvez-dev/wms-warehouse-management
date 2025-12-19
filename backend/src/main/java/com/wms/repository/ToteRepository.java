package com.wms.repository;

import com.wms.entity.Tote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToteRepository extends JpaRepository<Tote, Long> {

    Optional<Tote> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    @Query("SELECT t FROM Tote t " +
            "LEFT JOIN FETCH t.wave w " +
            "LEFT JOIN FETCH t.order o " +
            "LEFT JOIN FETCH t.packingStation " +
            "WHERE t.barcode = :barcode")
    Optional<Tote> findByBarcodeWithDetails(@Param("barcode") String barcode);
}
