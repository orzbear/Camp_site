package com.outscout.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Campsite entity representing individual camping spots within parks
 */
@Entity
@Table(name = "campsites")
public class Campsite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_id", nullable = false)
    private Park park;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(precision = 9, scale = 6)
    private BigDecimal lat;
    
    @Column(precision = 9, scale = 6)
    private BigDecimal lon;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "fee_aud", precision = 10, scale = 2)
    private BigDecimal feeAud;
    
    @Column(name = "pet_allowed")
    private Boolean petAllowed;
    
    @Column
    private Boolean bookable;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @OneToOne(mappedBy = "campsite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Amenities amenities;
    
    // Constructors
    public Campsite() {}
    
    public Campsite(Park park, String name, BigDecimal lat, BigDecimal lon, String description, 
                   BigDecimal feeAud, Boolean petAllowed, Boolean bookable) {
        this.park = park;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.feeAud = feeAud;
        this.petAllowed = petAllowed;
        this.bookable = bookable;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Park getPark() {
        return park;
    }
    
    public void setPark(Park park) {
        this.park = park;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getLat() {
        return lat;
    }
    
    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }
    
    public BigDecimal getLon() {
        return lon;
    }
    
    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getFeeAud() {
        return feeAud;
    }
    
    public void setFeeAud(BigDecimal feeAud) {
        this.feeAud = feeAud;
    }
    
    public Boolean getPetAllowed() {
        return petAllowed;
    }
    
    public void setPetAllowed(Boolean petAllowed) {
        this.petAllowed = petAllowed;
    }
    
    public Boolean getBookable() {
        return bookable;
    }
    
    public void setBookable(Boolean bookable) {
        this.bookable = bookable;
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
    
    public Amenities getAmenities() {
        return amenities;
    }
    
    public void setAmenities(Amenities amenities) {
        this.amenities = amenities;
    }
}
