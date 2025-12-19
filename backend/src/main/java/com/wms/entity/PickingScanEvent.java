package com.wms.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "picking_scan_events")
public class PickingScanEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PickingSession session;

    @Column(name = "scanned_code", nullable = false, length = 100)
    private String scannedCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private PickingScanEventType eventType;

    @Column(length = 255)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PickingSession getSession() {
        return session;
    }

    public void setSession(PickingSession session) {
        this.session = session;
    }

    public String getScannedCode() {
        return scannedCode;
    }

    public void setScannedCode(String scannedCode) {
        this.scannedCode = scannedCode;
    }

    public PickingScanEventType getEventType() {
        return eventType;
    }

    public void setEventType(PickingScanEventType eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

