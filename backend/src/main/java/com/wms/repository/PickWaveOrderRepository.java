package com.wms.repository;

import com.wms.entity.PickWaveOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickWaveOrderRepository extends JpaRepository<PickWaveOrder, Long> {

    boolean existsByOrderId(Long orderId);

    Optional<PickWaveOrder> findByOrderId(Long orderId);
}
