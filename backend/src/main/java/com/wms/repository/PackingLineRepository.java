package com.wms.repository;

import com.wms.entity.PackingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackingLineRepository extends JpaRepository<PackingLine, Long> {

    Optional<PackingLine> findBySessionIdAndProductId(Long sessionId, Long productId);
}
