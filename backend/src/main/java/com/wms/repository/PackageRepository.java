package com.wms.repository;

import com.wms.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByTrackingCode(String trackingCode);

    boolean existsByTrackingCode(String trackingCode);
}
