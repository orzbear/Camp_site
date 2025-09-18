package com.outscout.api.repo;

import com.outscout.api.model.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for Forecast entities
 */
@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    
    /**
     * Find forecasts for a specific location and time range
     */
    @Query("SELECT f FROM Forecast f WHERE " +
           "f.lat = :lat AND f.lon = :lon AND " +
           "f.hourUtc BETWEEN :fromTime AND :toTime " +
           "ORDER BY f.hourUtc")
    List<Forecast> findByLocationAndTimeRange(@Param("lat") BigDecimal lat,
                                            @Param("lon") BigDecimal lon,
                                            @Param("fromTime") OffsetDateTime fromTime,
                                            @Param("toTime") OffsetDateTime toTime);
    
    /**
     * Find the most recent forecast for a location
     */
    @Query("SELECT f FROM Forecast f WHERE " +
           "f.lat = :lat AND f.lon = :lon " +
           "ORDER BY f.fetchedAt DESC")
    List<Forecast> findMostRecentByLocation(@Param("lat") BigDecimal lat,
                                          @Param("lon") BigDecimal lon);
    
    /**
     * Delete old forecasts (cleanup)
     */
    @Query("DELETE FROM Forecast f WHERE f.fetchedAt < :cutoffTime")
    void deleteOldForecasts(@Param("cutoffTime") OffsetDateTime cutoffTime);
    
    /**
     * Check if forecast exists for location and time
     */
    boolean existsByLatAndLonAndHourUtc(BigDecimal lat, BigDecimal lon, OffsetDateTime hourUtc);
}
