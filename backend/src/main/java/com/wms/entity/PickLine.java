package com.wms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pick_lines")
public class PickLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_task_id", nullable = false)
    private PickTask pickTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_line_id", nullable = false)
    private OrderLine orderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "assigned_qty", nullable = false)
    private Integer assignedQty;

    @Column(name = "picked_qty", nullable = false)
    private Integer pickedQty = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PickLineStatus status = PickLineStatus.OPEN;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PickTask getPickTask() {
        return pickTask;
    }

    public void setPickTask(PickTask pickTask) {
        this.pickTask = pickTask;
    }

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getAssignedQty() {
        return assignedQty;
    }

    public void setAssignedQty(Integer assignedQty) {
        this.assignedQty = assignedQty;
    }

    public Integer getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(Integer pickedQty) {
        this.pickedQty = pickedQty;
    }

    public PickLineStatus getStatus() {
        return status;
    }

    public void setStatus(PickLineStatus status) {
        this.status = status;
    }
}
