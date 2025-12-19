package com.wms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "packing_lines", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "session_id", "product_id" })
})
public class PackingLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PackingSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "required_qty", nullable = false)
    private Integer requiredQty;

    @Column(name = "packed_qty", nullable = false)
    private Integer packedQty = 0;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackingSession getSession() {
        return session;
    }

    public void setSession(PackingSession session) {
        this.session = session;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getRequiredQty() {
        return requiredQty;
    }

    public void setRequiredQty(Integer requiredQty) {
        this.requiredQty = requiredQty;
    }

    public Integer getPackedQty() {
        return packedQty;
    }

    public void setPackedQty(Integer packedQty) {
        this.packedQty = packedQty;
    }
}
