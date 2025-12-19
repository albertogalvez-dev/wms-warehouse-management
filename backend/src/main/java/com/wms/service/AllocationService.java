package com.wms.service;

import com.wms.entity.*;
import com.wms.exception.InvalidOperationException;
import com.wms.repository.PickTaskRepository;
import com.wms.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AllocationService {

    private final StockRepository stockRepository;
    private final PickTaskRepository pickTaskRepository;

    public AllocationService(StockRepository stockRepository,
            PickTaskRepository pickTaskRepository) {
        this.stockRepository = stockRepository;
        this.pickTaskRepository = pickTaskRepository;
    }

    /**
     * Allocates stock to an order and creates PickTask with PickLines.
     * Strategy: For each order line, find stock ordered by location.code ascending,
     * allocate available stock until requested qty is met (or all stock consumed).
     * Does NOT deduct stock - that happens during actual pick confirmation.
     */
    public PickTask allocateOrder(Order order) {
        PickTask pickTask = new PickTask();
        pickTask.setOrder(order);
        pickTask.setStatus(PickTaskStatus.OPEN);

        int totalAllocated = 0;

        for (OrderLine orderLine : order.getLines()) {
            int remaining = orderLine.getRequestedQty();
            int lineAllocated = 0;

            // Get all stock for this product, sorted by location code
            List<Stock> stockList = stockRepository.findAll().stream()
                    .filter(s -> s.getProduct().getId().equals(orderLine.getProduct().getId()))
                    .filter(s -> s.getQuantity() > 0)
                    .filter(s -> s.getLocation().getActive())
                    .sorted(Comparator.comparing(s -> s.getLocation().getCode()))
                    .toList();

            for (Stock stock : stockList) {
                if (remaining <= 0)
                    break;

                int toAllocate = Math.min(remaining, stock.getQuantity());

                PickLine pickLine = new PickLine();
                pickLine.setOrderLine(orderLine);
                pickLine.setProduct(orderLine.getProduct());
                pickLine.setLocation(stock.getLocation());
                pickLine.setAssignedQty(toAllocate);
                pickLine.setPickedQty(0);
                pickLine.setStatus(PickLineStatus.OPEN);
                pickTask.addLine(pickLine);

                remaining -= toAllocate;
                lineAllocated += toAllocate;
                totalAllocated += toAllocate;
            }

            orderLine.setAllocatedQty(lineAllocated);
        }

        if (totalAllocated == 0) {
            throw new InvalidOperationException("No stock available to allocate for this order");
        }

        return pickTaskRepository.save(pickTask);
    }
}
