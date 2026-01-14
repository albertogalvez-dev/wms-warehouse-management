package com.wms.service;

import com.wms.dto.ProductInventorySummary;
import com.wms.entity.OrderStatus;
import com.wms.repository.OrderLineRepository;
import com.wms.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProductInventoryService {

    private static final EnumSet<OrderStatus> ALLOCATED_STATUSES = EnumSet.of(
            OrderStatus.RELEASED,
            OrderStatus.PICKING,
            OrderStatus.PICKED,
            OrderStatus.PACKING,
            OrderStatus.PACKED);

    private final StockRepository stockRepository;
    private final OrderLineRepository orderLineRepository;

    public ProductInventoryService(StockRepository stockRepository, OrderLineRepository orderLineRepository) {
        this.stockRepository = stockRepository;
        this.orderLineRepository = orderLineRepository;
    }

    public Map<Long, ProductInventorySummary> loadSummaries(Collection<Long> productIds) {
        Map<Long, ProductInventorySummary> summaries = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return summaries;
        }

        List<StockRepository.StockSummaryRow> stockRows = stockRepository.findStockSummaryByProductIds(productIds);
        for (StockRepository.StockSummaryRow row : stockRows) {
            Long productId = row.getProductId();
            Integer onHand = row.getQuantity() != null ? row.getQuantity().intValue() : 0;
            summaries.put(productId, new ProductInventorySummary(row.getLocationCode(), onHand, 0));
        }

        List<OrderLineRepository.AllocatedSummaryRow> allocatedRows = orderLineRepository
                .sumAllocatedByProductIds(productIds, ALLOCATED_STATUSES);

        for (OrderLineRepository.AllocatedSummaryRow row : allocatedRows) {
            Long productId = row.getProductId();
            Integer allocated = row.getAllocatedQty() != null ? row.getAllocatedQty().intValue() : 0;
            ProductInventorySummary summary = summaries.get(productId);
            if (summary == null) {
                summary = new ProductInventorySummary(null, 0, allocated);
                summaries.put(productId, summary);
            } else {
                summary.setStockAllocated(allocated);
                int available = summary.getStockOnHand() - allocated;
                summary.setStockAvailable(Math.max(available, 0));
            }
        }

        return summaries;
    }

    public ProductInventorySummary loadSummary(Long productId) {
        if (productId == null) {
            return new ProductInventorySummary(null, 0, 0);
        }
        Map<Long, ProductInventorySummary> summary = loadSummaries(List.of(productId));
        return summary.getOrDefault(productId, new ProductInventorySummary(null, 0, 0));
    }
}
