package com.wms.repository;

import com.wms.entity.PickWave;
import com.wms.entity.PickWaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickWaveRepository extends JpaRepository<PickWave, Long> {

    Optional<PickWave> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT pw FROM PickWave pw WHERE (:status IS NULL OR pw.status = :status)")
    Page<PickWave> findByStatusFilter(@Param("status") PickWaveStatus status, Pageable pageable);

    // Split into two queries to avoid MultipleBagFetchException
    @Query("SELECT DISTINCT pw FROM PickWave pw " +
            "LEFT JOIN FETCH pw.waveOrders wo " +
            "LEFT JOIN FETCH wo.order " +
            "WHERE pw.id = :id")
    Optional<PickWave> findByIdWithOrders(@Param("id") Long id);

    @Query("SELECT DISTINCT pw FROM PickWave pw " +
            "LEFT JOIN FETCH pw.totes t " +
            "LEFT JOIN FETCH t.packingStation " +
            "WHERE pw.id = :id")
    Optional<PickWave> findByIdWithTotes(@Param("id") Long id);
}
