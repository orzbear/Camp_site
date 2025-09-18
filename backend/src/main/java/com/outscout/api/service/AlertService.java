package com.outscout.api.service;

import com.outscout.api.model.entity.Alert;
import com.outscout.api.repo.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service for park alert operations
 */
@Service
@Transactional(readOnly = true)
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    /**
     * Get all active alerts
     */
    public List<Alert> getActiveAlerts() {
        logger.debug("Getting active alerts");
        return alertRepository.findActiveAlerts(OffsetDateTime.now());
    }
    
    /**
     * Get alerts for a specific park
     */
    public List<Alert> getAlertsByPark(Long parkId) {
        logger.debug("Getting alerts for park: {}", parkId);
        return alertRepository.findByParkId(parkId);
    }
    
    /**
     * Get alerts for a park within a time range
     */
    public List<Alert> getAlertsByParkAndTimeRange(Long parkId, OffsetDateTime startTime, OffsetDateTime endTime) {
        logger.debug("Getting alerts for park {} between {} and {}", parkId, startTime, endTime);
        return alertRepository.findByParkAndTimeRange(parkId, startTime, endTime);
    }
    
    /**
     * Get alerts by severity
     */
    public List<Alert> getAlertsBySeverity(String severity) {
        logger.debug("Getting alerts by severity: {}", severity);
        return alertRepository.findBySeverity(severity);
    }
    
    /**
     * Clean up old alerts
     */
    @Transactional
    public void cleanupOldAlerts() {
        OffsetDateTime cutoffTime = OffsetDateTime.now().minusDays(30);
        alertRepository.deleteOldAlerts(cutoffTime);
        logger.info("Cleaned up alerts older than {}", cutoffTime);
    }
}
