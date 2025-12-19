package com.wms.repository;

import com.wms.entity.PackingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackingStationRepository extends JpaRepository<PackingStation, Long> {

    Optional<PackingStation> findByCode(String code);

    boolean existsByCode(String code);

    List<PackingStation> findByActiveTrue();
}
