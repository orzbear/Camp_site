package com.outscout.api.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Amenities entity representing facilities available at a campsite
 */
@Entity
@Table(name = "amenities")
public class Amenities {
    
    @Id
    @Column(name = "campsite_id")
    private Long campsiteId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campsite_id")
    @MapsId
    private Campsite campsite;
    
    @Column(name = "has_bbq")
    private Boolean hasBbq = false;
    
    @Column(name = "has_toilet")
    private Boolean hasToilet = false;
    
    @Column(name = "has_water")
    private Boolean hasWater = false;
    
    @Column(name = "has_shelter")
    private Boolean hasShelter = false;
    
    @Column(name = "has_power")
    private Boolean hasPower = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    // Constructors
    public Amenities() {}
    
    public Amenities(Campsite campsite, Boolean hasBbq, Boolean hasToilet, Boolean hasWater, 
                    Boolean hasShelter, Boolean hasPower) {
        this.campsite = campsite;
        this.hasBbq = hasBbq;
        this.hasToilet = hasToilet;
        this.hasWater = hasWater;
        this.hasShelter = hasShelter;
        this.hasPower = hasPower;
    }
    
    // Getters and Setters
    public Long getCampsiteId() {
        return campsiteId;
    }
    
    public void setCampsiteId(Long campsiteId) {
        this.campsiteId = campsiteId;
    }
    
    public Campsite getCampsite() {
        return campsite;
    }
    
    public void setCampsite(Campsite campsite) {
        this.campsite = campsite;
    }
    
    public Boolean getHasBbq() {
        return hasBbq;
    }
    
    public void setHasBbq(Boolean hasBbq) {
        this.hasBbq = hasBbq;
    }
    
    public Boolean getHasToilet() {
        return hasToilet;
    }
    
    public void setHasToilet(Boolean hasToilet) {
        this.hasToilet = hasToilet;
    }
    
    public Boolean getHasWater() {
        return hasWater;
    }
    
    public void setHasWater(Boolean hasWater) {
        this.hasWater = hasWater;
    }
    
    public Boolean getHasShelter() {
        return hasShelter;
    }
    
    public void setHasShelter(Boolean hasShelter) {
        this.hasShelter = hasShelter;
    }
    
    public Boolean getHasPower() {
        return hasPower;
    }
    
    public void setHasPower(Boolean hasPower) {
        this.hasPower = hasPower;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
