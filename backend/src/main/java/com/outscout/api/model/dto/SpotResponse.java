package com.outscout.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Response DTO for campsite details
 */
public class SpotResponse {
    
    private Long id;
    private String name;
    private BigDecimal lat;
    private BigDecimal lon;
    private String description;
    private BigDecimal feeAud;
    private Boolean petAllowed;
    private Boolean bookable;
    
    private ParkInfo park;
    private AmenitiesInfo amenities;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;
    
    // Nested classes for related data
    public static class ParkInfo {
        private Long id;
        private String name;
        private String region;
        private String authority;
        private String websiteUrl;
        
        // Constructors
        public ParkInfo() {}
        
        public ParkInfo(Long id, String name, String region, String authority, String websiteUrl) {
            this.id = id;
            this.name = name;
            this.region = region;
            this.authority = authority;
            this.websiteUrl = websiteUrl;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getAuthority() { return authority; }
        public void setAuthority(String authority) { this.authority = authority; }
        public String getWebsiteUrl() { return websiteUrl; }
        public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    }
    
    public static class AmenitiesInfo {
        private Boolean hasBbq;
        private Boolean hasToilet;
        private Boolean hasWater;
        private Boolean hasShelter;
        private Boolean hasPower;
        
        // Constructors
        public AmenitiesInfo() {}
        
        public AmenitiesInfo(Boolean hasBbq, Boolean hasToilet, Boolean hasWater, 
                           Boolean hasShelter, Boolean hasPower) {
            this.hasBbq = hasBbq;
            this.hasToilet = hasToilet;
            this.hasWater = hasWater;
            this.hasShelter = hasShelter;
            this.hasPower = hasPower;
        }
        
        // Getters and Setters
        public Boolean getHasBbq() { return hasBbq; }
        public void setHasBbq(Boolean hasBbq) { this.hasBbq = hasBbq; }
        public Boolean getHasToilet() { return hasToilet; }
        public void setHasToilet(Boolean hasToilet) { this.hasToilet = hasToilet; }
        public Boolean getHasWater() { return hasWater; }
        public void setHasWater(Boolean hasWater) { this.hasWater = hasWater; }
        public Boolean getHasShelter() { return hasShelter; }
        public void setHasShelter(Boolean hasShelter) { this.hasShelter = hasShelter; }
        public Boolean getHasPower() { return hasPower; }
        public void setHasPower(Boolean hasPower) { this.hasPower = hasPower; }
    }
    
    // Constructors
    public SpotResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public ParkInfo getPark() {
        return park;
    }
    
    public void setPark(ParkInfo park) {
        this.park = park;
    }
    
    public AmenitiesInfo getAmenities() {
        return amenities;
    }
    
    public void setAmenities(AmenitiesInfo amenities) {
        this.amenities = amenities;
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
