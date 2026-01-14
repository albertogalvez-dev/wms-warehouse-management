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

    interface PackerStatsRow {
        String getOperator();

        Long getLinesPacked();

        Long getLineCount();

        Double getSecondsWorked();
    }

    @Query(value = "SELECT ps.operator AS operator, " +
            "SUM(pl.packed_qty) AS linesPacked, " +
            "COUNT(pl.id) AS lineCount, " +
            "SUM(EXTRACT(EPOCH FROM (COALESCE(ps.finished_at, ps.updated_at) - ps.started_at))) AS secondsWorked " +
            "FROM packing_sessions ps " +
            "JOIN packing_lines pl ON pl.session_id = ps.id " +
            "WHERE ps.operator IS NOT NULL " +
            "AND ps.started_at >= :fromDate " +
            "AND ps.started_at < :toDate " +
            "GROUP BY ps.operator " +
            "ORDER BY linesPacked DESC", nativeQuery = true)
    java.util.List<PackerStatsRow> findPackerStats(
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate);
}
