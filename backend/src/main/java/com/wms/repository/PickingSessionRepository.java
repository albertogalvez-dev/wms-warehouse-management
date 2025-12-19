package com.wms.repository;

import com.wms.entity.PickingSession;
import com.wms.entity.PickingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickingSessionRepository extends JpaRepository<PickingSession, Long> {

    Optional<PickingSession> findByWaveIdAndStatus(Long waveId, PickingSessionStatus status);

    @Query("SELECT ps FROM PickingSession ps " +
            "LEFT JOIN FETCH ps.wave " +
            "LEFT JOIN FETCH ps.currentLocation " +
            "LEFT JOIN FETCH ps.currentProduct " +
            "WHERE ps.id = :id")
    Optional<PickingSession> findByIdWithDetails(@Param("id") Long id);
}

