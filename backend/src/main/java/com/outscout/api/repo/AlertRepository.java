package com.outscout.api.repo;

import com.outscout.api.model.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for Alert entities
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    /**
     * Find alerts by park
     */
    List<Alert> findByParkId(Long parkId);
    
    /**
     * Find active alerts (within time range)
     */
    @Query("SELECT a FROM Alert a WHERE " +
           "a.startsAt <= :now AND (a.endsAt IS NULL OR a.endsAt >= :now)")
    List<Alert> findActiveAlerts(@Param("now") OffsetDateTime now);
    
    /**
     * Find alerts by park and time range
     */
    @Query("SELECT a FROM Alert a WHERE " +
           "a.park.id = :parkId AND " +
           "a.startsAt <= :endTime AND (a.endsAt IS NULL OR a.endsAt >= :startTime)")
    List<Alert> findByParkAndTimeRange(@Param("parkId") Long parkId,
                                      @Param("startTime") OffsetDateTime startTime,
                                      @Param("endTime") OffsetDateTime endTime);
    
    /**
     * Find alerts by severity
     */
    List<Alert> findBySeverity(String severity);
    
    /**
     * Find alerts by source
     */
    List<Alert> findBySource(String source);
    
    /**
     * Delete old alerts (cleanup)
     */
    @Query("DELETE FROM Alert a WHERE a.endsAt < :cutoffTime")
    void deleteOldAlerts(@Param("cutoffTime") OffsetDateTime cutoffTime);
}
