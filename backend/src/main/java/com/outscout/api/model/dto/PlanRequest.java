package com.outscout.api.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request DTO for trip planning
 */
public class PlanRequest {
    
    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal lat;
    
    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal lon;
    
    @NotNull
    private OffsetDateTime from;
    
    @NotNull
    private OffsetDateTime to;
    
    private PlanPreferences prefs;
    
    // Nested preferences class
    public static class PlanPreferences {
        private Double maxWindMps = 8.0;
        private Double minTempC = 18.0;
        private Double maxTempC = 28.0;
        private Double maxUvIndex = 8.0;
        private Double maxPrecipProb = 0.3;
        
        // Constructors
        public PlanPreferences() {}
        
        // Getters and Setters
        public Double getMaxWindMps() { return maxWindMps; }
        public void setMaxWindMps(Double maxWindMps) { this.maxWindMps = maxWindMps; }
        public Double getMinTempC() { return minTempC; }
        public void setMinTempC(Double minTempC) { this.minTempC = minTempC; }
        public Double getMaxTempC() { return maxTempC; }
        public void setMaxTempC(Double maxTempC) { this.maxTempC = maxTempC; }
        public Double getMaxUvIndex() { return maxUvIndex; }
        public void setMaxUvIndex(Double maxUvIndex) { this.maxUvIndex = maxUvIndex; }
        public Double getMaxPrecipProb() { return maxPrecipProb; }
        public void setMaxPrecipProb(Double maxPrecipProb) { this.maxPrecipProb = maxPrecipProb; }
    }
    
    // Constructors
    public PlanRequest() {}
    
    // Getters and Setters
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
    
    public OffsetDateTime getFrom() {
        return from;
    }
    
    public void setFrom(OffsetDateTime from) {
        this.from = from;
    }
    
    public OffsetDateTime getTo() {
        return to;
    }
    
    public void setTo(OffsetDateTime to) {
        this.to = to;
    }
    
    public PlanPreferences getPrefs() {
        return prefs;
    }
    
    public void setPrefs(PlanPreferences prefs) {
        this.prefs = prefs;
    }
}
