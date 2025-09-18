package com.outscout.api.controller;

import com.outscout.api.model.entity.Alert;
import com.outscout.api.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST controller for park alert operations
 */
@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AlertController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);
    
    @Autowired
    private AlertService alertService;
    
    /**
     * Get all active alerts
     */
    @GetMapping
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        logger.info("Getting active alerts");
        
        try {
            List<Alert> alerts = alertService.getActiveAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error getting active alerts", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get alerts for a specific park
     */
    @GetMapping("/park/{parkId}")
    public ResponseEntity<List<Alert>> getAlertsByPark(@PathVariable Long parkId) {
        logger.info("Getting alerts for park: {}", parkId);
        
        try {
            List<Alert> alerts = alertService.getAlertsByPark(parkId);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error getting alerts for park: {}", parkId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get alerts for a park within a time range
     */
    @GetMapping("/park/{parkId}/range")
    public ResponseEntity<List<Alert>> getAlertsByParkAndTimeRange(
            @PathVariable Long parkId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        
        logger.info("Getting alerts for park {} between {} and {}", parkId, startTime, endTime);
        
        try {
            List<Alert> alerts = alertService.getAlertsByParkAndTimeRange(parkId, startTime, endTime);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error getting alerts for park {} in time range", parkId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get alerts by severity
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Alert>> getAlertsBySeverity(@PathVariable String severity) {
        logger.info("Getting alerts by severity: {}", severity);
        
        try {
            List<Alert> alerts = alertService.getAlertsBySeverity(severity);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error getting alerts by severity: {}", severity, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
