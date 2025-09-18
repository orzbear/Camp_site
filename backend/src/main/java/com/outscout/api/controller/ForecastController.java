package com.outscout.api.controller;

import com.outscout.api.model.entity.Forecast;
import com.outscout.api.service.ForecastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST controller for weather forecast operations
 */
@RestController
@RequestMapping("/forecast")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ForecastController {
    
    private static final Logger logger = LoggerFactory.getLogger(ForecastController.class);
    
    @Autowired
    private ForecastService forecastService;
    
    /**
     * Get forecast for a specific location and time range
     */
    @GetMapping
    public ResponseEntity<List<Forecast>> getForecast(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lon,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        
        logger.info("Getting forecast for location ({}, {}) from {} to {}", lat, lon, from, to);
        
        try {
            List<Forecast> forecasts = forecastService.getForecast(lat, lon, from, to);
            return ResponseEntity.ok(forecasts);
        } catch (Exception e) {
            logger.error("Error getting forecast for location ({}, {})", lat, lon, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
