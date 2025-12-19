package com.wms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "tracking_code" }),
        @UniqueConstraint(columnNames = { "shipment_id", "package_no" })
})
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "package_no", nullable = false)
    private Integer packageNo;

    @Column(name = "package_count", nullable = false)
    private Integer packageCount;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 100)
    private String trackingCode;

    @Column(name = "label_format", nullable = false, length = 10)
    private String labelFormat = "ZPL";

    @Column(name = "label_zpl", nullable = false, columnDefinition = "TEXT")
    private String labelZpl;

    @Column(name = "printed_at")
    private LocalDateTime printedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Integer getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(Integer packageNo) {
        this.packageNo = packageNo;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getLabelFormat() {
        return labelFormat;
    }

    public void setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
    }

    public String getLabelZpl() {
        return labelZpl;
    }

    public void setLabelZpl(String labelZpl) {
        this.labelZpl = labelZpl;
    }

    public LocalDateTime getPrintedAt() {
        return printedAt;
    }

    public void setPrintedAt(LocalDateTime printedAt) {
        this.printedAt = printedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
