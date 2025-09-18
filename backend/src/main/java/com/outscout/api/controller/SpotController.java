package com.outscout.api.controller;

import com.outscout.api.model.dto.SpotResponse;
import com.outscout.api.model.dto.SpotSearchRequest;
import com.outscout.api.service.SpotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for campsite operations
 */
@RestController
@RequestMapping("/spots")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class SpotController {
    
    private static final Logger logger = LoggerFactory.getLogger(SpotController.class);
    
    @Autowired
    private SpotService spotService;
    
    /**
     * Search campsites with filters and pagination
     */
    @GetMapping("/search")
    public ResponseEntity<Page<SpotResponse>> searchSpots(@Valid SpotSearchRequest request) {
        logger.info("Searching spots with request: {}", request);
        
        try {
            Page<SpotResponse> results = spotService.searchSpots(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error searching spots", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get campsite by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getSpot(@PathVariable Long id) {
        logger.info("Getting spot with ID: {}", id);
        
        try {
            Optional<SpotResponse> spot = spotService.getSpotById(id);
            return spot.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error getting spot with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
