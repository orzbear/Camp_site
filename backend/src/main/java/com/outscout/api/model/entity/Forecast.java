package com.outscout.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Forecast entity representing weather forecast data for specific locations and times
 */
@Entity
@Table(name = "forecasts", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"lat", "lon", "hour_utc"}))
public class Forecast {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal lat;
    
    @NotNull
    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal lon;
    
    @NotNull
    @Column(name = "hour_utc", nullable = false)
    private OffsetDateTime hourUtc;
    
    @Column(name = "temp_c")
    private Double tempC;
    
    @Column(name = "precip_mm")
    private Double precipMm;
    
    @Column(name = "precip_prob")
    private Double precipProb;
    
    @Column(name = "wind_mps")
    private Double windMps;
    
    @Column(name = "uv_index")
    private Double uvIndex;
    
    @Column(length = 32)
    private String source = "open-meteo";
    
    @CreationTimestamp
    @Column(name = "fetched_at", nullable = false, updatable = false)
    private OffsetDateTime fetchedAt;
    
    // Constructors
    public Forecast() {}
    
    public Forecast(BigDecimal lat, BigDecimal lon, OffsetDateTime hourUtc, Double tempC, 
                   Double precipMm, Double precipProb, Double windMps, Double uvIndex, String source) {
        this.lat = lat;
        this.lon = lon;
        this.hourUtc = hourUtc;
        this.tempC = tempC;
        this.precipMm = precipMm;
        this.precipProb = precipProb;
        this.windMps = windMps;
        this.uvIndex = uvIndex;
        this.source = source;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public OffsetDateTime getHourUtc() {
        return hourUtc;
    }
    
    public void setHourUtc(OffsetDateTime hourUtc) {
        this.hourUtc = hourUtc;
    }
    
    public Double getTempC() {
        return tempC;
    }
    
    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }
    
    public Double getPrecipMm() {
        return precipMm;
    }
    
    public void setPrecipMm(Double precipMm) {
        this.precipMm = precipMm;
    }
    
    public Double getPrecipProb() {
        return precipProb;
    }
    
    public void setPrecipProb(Double precipProb) {
        this.precipProb = precipProb;
    }
    
    public Double getWindMps() {
        return windMps;
    }
    
    public void setWindMps(Double windMps) {
        this.windMps = windMps;
    }
    
    public Double getUvIndex() {
        return uvIndex;
    }
    
    public void setUvIndex(Double uvIndex) {
        this.uvIndex = uvIndex;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public OffsetDateTime getFetchedAt() {
        return fetchedAt;
    }
    
    public void setFetchedAt(OffsetDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
