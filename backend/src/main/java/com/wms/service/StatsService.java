package com.wms.service;

import com.wms.dto.OrderStatsResponse;
import com.wms.dto.WorkerStatsResponse;
import com.wms.entity.OrderStatus;
import com.wms.repository.OrderLineRepository;
import com.wms.repository.OrderRepository;
import com.wms.repository.PackingSessionRepository;
import com.wms.repository.PickTaskRepository;
import com.wms.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ShipmentRepository shipmentRepository;
    private final PickTaskRepository pickTaskRepository;
    private final PackingSessionRepository packingSessionRepository;

    public StatsService(OrderRepository orderRepository,
            OrderLineRepository orderLineRepository,
            ShipmentRepository shipmentRepository,
            PickTaskRepository pickTaskRepository,
            PackingSessionRepository packingSessionRepository) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.shipmentRepository = shipmentRepository;
        this.pickTaskRepository = pickTaskRepository;
        this.packingSessionRepository = packingSessionRepository;
    }

    public OrderStatsResponse getOrderStats(LocalDate from, LocalDate to) {
        LocalDate rangeStart = from != null ? from : LocalDate.now().minusDays(6);
        LocalDate rangeEnd = to != null ? to : LocalDate.now();

        LocalDateTime start = rangeStart.atStartOfDay();
        LocalDateTime endExclusive = rangeEnd.plusDays(1).atStartOfDay();

        OrderStatsResponse response = new OrderStatsResponse();
        response.setRangeStart(rangeStart);
        response.setRangeEnd(rangeEnd);
        response.setOrdersInRange(orderRepository.countByCreatedAtBetween(start, endExclusive));

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        response.setOrdersToday(orderRepository.countByCreatedAtBetween(todayStart, tomorrowStart));

        LocalDate last7Start = today.minusDays(6);
        response.setOrdersLast7Days(orderRepository.countByCreatedAtBetween(last7Start.atStartOfDay(), tomorrowStart));

        EnumMap<OrderStatus, Long> statusCounts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.put(status, 0L);
        }
        List<OrderRepository.StatusCountRow> rows = orderRepository.countByStatus();
        for (OrderRepository.StatusCountRow row : rows) {
            if (row.getStatus() != null) {
                statusCounts.put(row.getStatus(), toLong(row.getCount()));
            }
        }
        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusMap.put(status.name(), statusCounts.getOrDefault(status, 0L));
        }
        response.setOrdersByStatus(statusMap);

        OrderLineRepository.LineSummaryRow lineSummary = orderLineRepository.sumPendingAndPicked();
        long pending = lineSummary != null ? toLong(lineSummary.getPendingQty()) : 0L;
        long picked = lineSummary != null ? toLong(lineSummary.getPickedQty()) : 0L;
        response.setLinesPending(pending);
        response.setLinesPicked(picked);

        Map<String, Long> shipmentsByCarrier = new LinkedHashMap<>();
        long shipmentTotal = 0;
        for (ShipmentRepository.CarrierCountRow row : shipmentRepository.countPendingByCarrier()) {
            if (row.getCarrier() != null) {
                long count = toLong(row.getCount());
                shipmentsByCarrier.put(row.getCarrier(), count);
                shipmentTotal += count;
            }
        }
        response.setShipmentsPendingByCarrier(shipmentsByCarrier);
        response.setShipmentsPendingTotal(shipmentTotal);

        return response;
    }

    public WorkerStatsResponse getWorkerStats(LocalDate from, LocalDate to) {
        LocalDate rangeStart = from != null ? from : LocalDate.now().minusDays(6);
        LocalDate rangeEnd = to != null ? to : LocalDate.now();

        LocalDateTime start = rangeStart.atStartOfDay();
        LocalDateTime endExclusive = rangeEnd.plusDays(1).atStartOfDay();

        WorkerStatsResponse response = new WorkerStatsResponse();
        response.setRangeStart(rangeStart);
        response.setRangeEnd(rangeEnd);

        List<WorkerStatsResponse.PickerStat> pickers = pickTaskRepository.findPickerStats(start, endExclusive)
                .stream()
                .map(row -> {
                    WorkerStatsResponse.PickerStat stat = new WorkerStatsResponse.PickerStat();
                    stat.setOperator(row.getOperator());
                    stat.setLinesPicked(toLong(row.getLinesPicked()));
                    stat.setLineCount(toLong(row.getLineCount()));
                    stat.setPicksPerHour(round1(perHour(stat.getLinesPicked(), row.getSecondsWorked())));
                    return stat;
                })
                .toList();
        response.setPicking(pickers);

        List<WorkerStatsResponse.PackerStat> packers = packingSessionRepository.findPackerStats(start, endExclusive)
                .stream()
                .map(row -> {
                    WorkerStatsResponse.PackerStat stat = new WorkerStatsResponse.PackerStat();
                    stat.setOperator(row.getOperator());
                    stat.setLinesPacked(toLong(row.getLinesPacked()));
                    stat.setLineCount(toLong(row.getLineCount()));
                    stat.setPacksPerHour(round1(perHour(stat.getLinesPacked(), row.getSecondsWorked())));
                    return stat;
                })
                .toList();
        response.setPacking(packers);

        return response;
    }

    private long toLong(Long value) {
        return value != null ? value : 0L;
    }

    private double perHour(Long lines, Double secondsWorked) {
        double units = lines != null ? lines.doubleValue() : 0.0;
        double seconds = secondsWorked != null ? secondsWorked : 0.0;
        double hours = seconds / 3600.0;
        if (hours <= 0.0) {
            return units;
        }
        return units / Math.max(hours, 0.1);
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
