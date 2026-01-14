package com.wms.repository;

import com.wms.entity.PickTask;
import com.wms.entity.PickTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PickTaskRepository extends JpaRepository<PickTask, Long> {

    @Query("SELECT pt FROM PickTask pt WHERE (:status IS NULL OR pt.status = :status)")
    Page<PickTask> findByStatusFilter(@Param("status") PickTaskStatus status, Pageable pageable);

    @Query("SELECT pt FROM PickTask pt " +
            "LEFT JOIN FETCH pt.order " +
            "LEFT JOIN FETCH pt.lines l " +
            "LEFT JOIN FETCH l.product " +
            "LEFT JOIN FETCH l.location " +
            "LEFT JOIN FETCH l.orderLine " +
            "WHERE pt.id = :id")
    Optional<PickTask> findByIdWithDetails(@Param("id") Long id);

    Optional<PickTask> findByOrderId(Long orderId);

    interface PickerStatsRow {
        String getOperator();

        Long getLinesPicked();

        Long getLineCount();

        Double getSecondsWorked();
    }

    @Query(value = "SELECT pt.operator AS operator, " +
            "SUM(pl.picked_qty) AS linesPicked, " +
            "COUNT(pl.id) AS lineCount, " +
            "SUM(EXTRACT(EPOCH FROM (pt.updated_at - pt.created_at))) AS secondsWorked " +
            "FROM pick_tasks pt " +
            "JOIN pick_lines pl ON pl.pick_task_id = pt.id " +
            "WHERE pt.operator IS NOT NULL " +
            "AND pt.created_at >= :fromDate " +
            "AND pt.created_at < :toDate " +
            "GROUP BY pt.operator " +
            "ORDER BY linesPicked DESC", nativeQuery = true)
    java.util.List<PickerStatsRow> findPickerStats(
            @Param("fromDate") java.time.LocalDateTime fromDate,
            @Param("toDate") java.time.LocalDateTime toDate);
}
