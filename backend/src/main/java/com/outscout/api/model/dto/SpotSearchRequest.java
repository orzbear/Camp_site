package com.outscout.api.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request DTO for campsite search
 */
public class SpotSearchRequest {
    
    private String query;
    private String region;
    private Boolean petAllowed;
    private Boolean bookable;
    private Boolean hasBbq;
    private Boolean hasToilet;
    private Boolean hasWater;
    private Boolean hasShelter;
    private Boolean hasPower;
    
    @Min(0)
    private int page = 0;
    
    @Min(1)
    @Max(100)
    private int size = 20;
    
    // Constructors
    public SpotSearchRequest() {}
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
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
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
}
