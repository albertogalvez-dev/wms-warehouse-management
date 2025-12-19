package com.wms.repository;

import com.wms.entity.Order;
import com.wms.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByExternalRef(String externalRef);

    boolean existsByExternalRef(String externalRef);

    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status)")
    Page<Order> findByStatusFilter(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.lines WHERE o.id = :id")
    Optional<Order> findByIdWithLines(@Param("id") Long id);
}
