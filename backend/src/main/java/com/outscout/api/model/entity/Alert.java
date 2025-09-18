package com.outscout.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Alert entity representing park alerts and warnings
 */
@Entity
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_id")
    private Park park;
    
    @NotBlank
    @Column(nullable = false, length = 16)
    private String severity;
    
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "starts_at")
    private OffsetDateTime startsAt;
    
    @Column(name = "ends_at")
    private OffsetDateTime endsAt;
    
    @Column(length = 64)
    private String source;
    
    @Column
    private String url;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @CreationTimestamp
    @Column(name = "fetched_at", nullable = false, updatable = false)
    private OffsetDateTime fetchedAt;
    
    // Constructors
    public Alert() {}
    
    public Alert(Park park, String severity, String title, String summary, 
                OffsetDateTime startsAt, OffsetDateTime endsAt, String source, String url) {
        this.park = park;
        this.severity = severity;
        this.title = title;
        this.summary = summary;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.source = source;
        this.url = url;
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
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public OffsetDateTime getStartsAt() {
        return startsAt;
    }
    
    public void setStartsAt(OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }
    
    public OffsetDateTime getEndsAt() {
        return endsAt;
    }
    
    public void setEndsAt(OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public OffsetDateTime getFetchedAt() {
        return fetchedAt;
    }
    
    public void setFetchedAt(OffsetDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
