package com.outscout.api.controller;

import com.outscout.api.model.dto.PlanRequest;
import com.outscout.api.model.dto.PlanResponse;
import com.outscout.api.service.PlanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for trip planning operations
 */
@RestController
@RequestMapping("/plan")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class PlanController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);
    
    @Autowired
    private PlanService planService;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * Create a new trip plan
     */
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody PlanRequest request) {
        logger.info("Creating plan for location ({}, {})", request.getLat(), request.getLon());
        
        try {
            String requestId = planService.createPlan(request);
            
            // Return initial response
            PlanResponse response = new PlanResponse();
            response.setRequestId(requestId);
            response.setStatus("processing");
            response.setMessage("Plan created, processing...");
            response.setWindows(new PlanResponse.WeatherWindow[0]);
            
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            logger.error("Error creating plan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get plan result by request ID
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<PlanResponse> getPlanResult(@PathVariable String requestId) {
        logger.debug("Getting plan result for requestId: {}", requestId);
        
        try {
            Optional<PlanResponse> result = planService.getPlanResult(requestId);
            return result.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error getting plan result for requestId: {}", requestId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Stream plan updates via Server-Sent Events
     */
    @GetMapping(value = "/stream/{requestId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPlanUpdates(@PathVariable String requestId) {
        logger.info("Starting SSE stream for plan requestId: {}", requestId);
        
        SseEmitter emitter = new SseEmitter(300000L); // 5 minute timeout
        
        // Schedule periodic updates
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Optional<PlanResponse> result = planService.getPlanResult(requestId);
                if (result.isPresent()) {
                    PlanResponse response = result.get();
                    
                    // Send update event
                    emitter.send(SseEmitter.event()
                        .name("update")
                        .data(response));
                    
                    // Close connection if completed or errored
                    if ("completed".equals(response.getStatus()) || "error".equals(response.getStatus())) {
                        emitter.complete();
                    }
                } else {
                    // Send keep-alive
                    emitter.send(SseEmitter.event()
                        .name("keepalive")
                        .data("{\"timestamp\":\"" + System.currentTimeMillis() + "\"}"));
                }
            } catch (IOException e) {
                logger.warn("Error sending SSE update for requestId: {}", requestId, e);
                emitter.completeWithError(e);
            } catch (Exception e) {
                logger.error("Unexpected error in SSE stream for requestId: {}", requestId, e);
                emitter.completeWithError(e);
            }
        }, 0, 2, TimeUnit.SECONDS); // Send updates every 2 seconds
        
        // Handle completion and cleanup
        emitter.onCompletion(() -> {
            logger.debug("SSE stream completed for requestId: {}", requestId);
        });
        
        emitter.onTimeout(() -> {
            logger.warn("SSE stream timeout for requestId: {}", requestId);
        });
        
        emitter.onError((ex) -> {
            logger.error("SSE stream error for requestId: {}", requestId, ex);
        });
        
        return emitter;
    }
}
