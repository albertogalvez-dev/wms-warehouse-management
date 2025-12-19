package com.wms.repository;

import com.wms.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByOrderId(Long orderId);

    @Query("SELECT s FROM Shipment s " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.packages " +
            "WHERE o.id = :orderId")
    Optional<Shipment> findByOrderIdWithPackages(@Param("orderId") Long orderId);

    @Query("SELECT s FROM Shipment s " +
            "LEFT JOIN FETCH s.order o " +
            "LEFT JOIN FETCH s.packages " +
            "WHERE s.id = :id")
    Optional<Shipment> findByIdWithPackages(@Param("id") Long id);
}
