package com.wms.repository;

import com.wms.entity.PickLine;
import com.wms.entity.PickLineStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickLineRepository extends JpaRepository<PickLine, Long> {

    interface ToteCandidateRow {
        String getToteBarcode();

        Long getOrderId();

        String getExternalRef();

        Long getRemainingQty();
    }

    @Query("SELECT t.barcode AS toteBarcode, o.id AS orderId, o.externalRef AS externalRef, " +
            "SUM(pl.assignedQty - pl.pickedQty) AS remainingQty " +
            "FROM Tote t " +
            "JOIN t.order o " +
            "JOIN PickTask pt ON pt.order = o " +
            "JOIN pt.lines pl " +
            "WHERE t.wave.id = :waveId " +
            "AND pl.location.id = :locationId " +
            "AND pl.product.id = :productId " +
            "AND pl.status = com.wms.entity.PickLineStatus.OPEN " +
            "GROUP BY t.barcode, o.id, o.externalRef " +
            "ORDER BY t.barcode ASC")
    List<ToteCandidateRow> findCandidateTotesForWaveLocationProduct(
            @Param("waveId") Long waveId,
            @Param("locationId") Long locationId,
            @Param("productId") Long productId);

    @Query("SELECT l.code FROM Tote t " +
            "JOIN t.order o " +
            "JOIN PickTask pt ON pt.order = o " +
            "JOIN pt.lines pl " +
            "JOIN pl.location l " +
            "WHERE t.wave.id = :waveId " +
            "AND pl.status = com.wms.entity.PickLineStatus.OPEN " +
            "GROUP BY l.code " +
            "ORDER BY l.code ASC")
    List<String> findNextPendingLocations(@Param("waveId") Long waveId, Pageable pageable);

    @Query("SELECT COUNT(pl) FROM Tote t " +
            "JOIN t.order o " +
            "JOIN PickTask pt ON pt.order = o " +
            "JOIN pt.lines pl " +
            "WHERE t.wave.id = :waveId " +
            "AND pl.status = :status")
    long countPickLinesByWaveAndStatus(@Param("waveId") Long waveId, @Param("status") PickLineStatus status);

    Optional<PickLine> findFirstByPickTaskOrderIdAndLocationIdAndProductIdAndStatusOrderByIdAsc(
            Long orderId, Long locationId, Long productId, PickLineStatus status);

    boolean existsByPickTaskIdAndStatus(Long pickTaskId, PickLineStatus status);
}
