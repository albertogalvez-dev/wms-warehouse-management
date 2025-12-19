package com.wms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_waves")
public class PickWave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PickWaveStatus status = PickWaveStatus.PLANNED;

    @OneToMany(mappedBy = "wave", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PickWaveOrder> waveOrders = new ArrayList<>();

    @OneToMany(mappedBy = "wave", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tote> totes = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addWaveOrder(PickWaveOrder waveOrder) {
        waveOrders.add(waveOrder);
        waveOrder.setWave(this);
    }

    public void addTote(Tote tote) {
        totes.add(tote);
        tote.setWave(this);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PickWaveStatus getStatus() {
        return status;
    }

    public void setStatus(PickWaveStatus status) {
        this.status = status;
    }

    public List<PickWaveOrder> getWaveOrders() {
        return waveOrders;
    }

    public void setWaveOrders(List<PickWaveOrder> waveOrders) {
        this.waveOrders = waveOrders;
    }

    public List<Tote> getTotes() {
        return totes;
    }

    public void setTotes(List<Tote> totes) {
        this.totes = totes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
