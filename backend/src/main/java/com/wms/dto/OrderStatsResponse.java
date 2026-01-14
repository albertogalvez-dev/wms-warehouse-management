package com.wms.dto;

import java.time.LocalDate;
import java.util.Map;

public class OrderStatsResponse {
    private LocalDate rangeStart;
    private LocalDate rangeEnd;
    private Long ordersInRange;
    private Long ordersToday;
    private Long ordersLast7Days;
    private Map<String, Long> ordersByStatus;
    private Long linesPending;
    private Long linesPicked;
    private Map<String, Long> shipmentsPendingByCarrier;
    private Long shipmentsPendingTotal;

    public LocalDate getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(LocalDate rangeStart) {
        this.rangeStart = rangeStart;
    }

    public LocalDate getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(LocalDate rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public Long getOrdersInRange() {
        return ordersInRange;
    }

    public void setOrdersInRange(Long ordersInRange) {
        this.ordersInRange = ordersInRange;
    }

    public Long getOrdersToday() {
        return ordersToday;
    }

    public void setOrdersToday(Long ordersToday) {
        this.ordersToday = ordersToday;
    }

    public Long getOrdersLast7Days() {
        return ordersLast7Days;
    }

    public void setOrdersLast7Days(Long ordersLast7Days) {
        this.ordersLast7Days = ordersLast7Days;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public Long getLinesPending() {
        return linesPending;
    }

    public void setLinesPending(Long linesPending) {
        this.linesPending = linesPending;
    }

    public Long getLinesPicked() {
        return linesPicked;
    }

    public void setLinesPicked(Long linesPicked) {
        this.linesPicked = linesPicked;
    }

    public Map<String, Long> getShipmentsPendingByCarrier() {
        return shipmentsPendingByCarrier;
    }

    public void setShipmentsPendingByCarrier(Map<String, Long> shipmentsPendingByCarrier) {
        this.shipmentsPendingByCarrier = shipmentsPendingByCarrier;
    }

    public Long getShipmentsPendingTotal() {
        return shipmentsPendingTotal;
    }

    public void setShipmentsPendingTotal(Long shipmentsPendingTotal) {
        this.shipmentsPendingTotal = shipmentsPendingTotal;
    }
}
