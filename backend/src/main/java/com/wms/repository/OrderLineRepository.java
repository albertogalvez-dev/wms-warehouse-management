package com.wms.repository;

import com.wms.entity.OrderLine;
import com.wms.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    interface AllocatedSummaryRow {
        Long getProductId();
        Long getAllocatedQty();
    }

    interface LineSummaryRow {
        Long getPendingQty();
        Long getPickedQty();
    }

    @Query("SELECT ol.product.id as productId, SUM(ol.allocatedQty) as allocatedQty " +
            "FROM OrderLine ol JOIN ol.order o " +
            "WHERE o.status IN :statuses AND ol.product.id IN :productIds " +
            "GROUP BY ol.product.id")
    List<AllocatedSummaryRow> sumAllocatedByProductIds(
            @Param("productIds") Collection<Long> productIds,
            @Param("statuses") Collection<OrderStatus> statuses);

    @Query("SELECT SUM(ol.requestedQty - ol.pickedQty) as pendingQty, " +
            "SUM(ol.pickedQty) as pickedQty " +
            "FROM OrderLine ol JOIN ol.order o " +
            "WHERE o.status <> com.wms.entity.OrderStatus.CANCELLED")
    LineSummaryRow sumPendingAndPicked();
}
