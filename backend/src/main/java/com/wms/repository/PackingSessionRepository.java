package com.wms.repository;

import com.wms.entity.PackingSession;
import com.wms.entity.PackingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackingSessionRepository extends JpaRepository<PackingSession, Long> {

    Optional<PackingSession> findByToteIdAndStatus(Long toteId, PackingSessionStatus status);

    @Query("SELECT ps FROM PackingSession ps " +
            "LEFT JOIN FETCH ps.tote t " +
            "LEFT JOIN FETCH t.order o " +
            "LEFT JOIN FETCH ps.station " +
            "LEFT JOIN FETCH ps.lines l " +
            "LEFT JOIN FETCH l.product " +
            "WHERE ps.id = :id")
    Optional<PackingSession> findByIdWithDetails(@Param("id") Long id);

    boolean existsByToteIdAndStatus(Long toteId, PackingSessionStatus status);
}
