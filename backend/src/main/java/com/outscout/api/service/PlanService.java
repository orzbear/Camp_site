package com.outscout.api.service;

import com.outscout.api.model.dto.PlanRequest;
import com.outscout.api.model.dto.PlanResponse;
import com.outscout.api.model.entity.Forecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for trip planning operations
 */
@Service
public class PlanService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);
    
    @Autowired
    private ForecastService forecastService;
    
    // In-memory storage for plan results (MVP approach)
    private final Map<String, PlanResponse> planResults = new ConcurrentHashMap<>();
    
    /**
     * Create a new trip plan
     */
    public String createPlan(PlanRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Creating plan with requestId: {}", requestId);
        
        // Initialize response
        PlanResponse response = new PlanResponse();
        response.setRequestId(requestId);
        response.setStatus("processing");
        response.setMessage("Analyzing weather conditions...");
        response.setWindows(new PlanResponse.WeatherWindow[0]);
        
        planResults.put(requestId, response);
        
        // Process asynchronously
        CompletableFuture.runAsync(() -> processPlan(requestId, request));
        
        return requestId;
    }
    
    /**
     * Get plan result by request ID
     */
    public Optional<PlanResponse> getPlanResult(String requestId) {
        return Optional.ofNullable(planResults.get(requestId));
    }
    
    /**
     * Process the plan asynchronously
     */
    private void processPlan(String requestId, PlanRequest request) {
        try {
            logger.debug("Processing plan {} for location ({}, {})", requestId, request.getLat(), request.getLon());
            
            // Update status
            updatePlanStatus(requestId, "processing", "Fetching weather data...");
            
            // Get forecast data
            List<Forecast> forecasts = forecastService.getForecast(
                request.getLat(), 
                request.getLon(), 
                request.getFrom(), 
                request.getTo()
            );
            
            if (forecasts.isEmpty()) {
                updatePlanStatus(requestId, "error", "No weather data available for the specified location and time range.");
                return;
            }
            
            // Update status
            updatePlanStatus(requestId, "processing", "Analyzing weather windows...");
            
            // Analyze weather windows
            List<PlanResponse.WeatherWindow> windows = analyzeWeatherWindows(forecasts, request);
            
            // Update final result
            PlanResponse finalResponse = new PlanResponse();
            finalResponse.setRequestId(requestId);
            finalResponse.setStatus("completed");
            finalResponse.setMessage("Analysis complete");
            finalResponse.setWindows(windows.toArray(new PlanResponse.WeatherWindow[0]));
            
            planResults.put(requestId, finalResponse);
            logger.info("Plan {} completed with {} weather windows", requestId, windows.size());
            
        } catch (Exception e) {
            logger.error("Error processing plan {}", requestId, e);
            updatePlanStatus(requestId, "error", "An error occurred while processing the plan: " + e.getMessage());
        }
    }
    
    /**
     * Update plan status
     */
    private void updatePlanStatus(String requestId, String status, String message) {
        PlanResponse response = planResults.get(requestId);
        if (response != null) {
            response.setStatus(status);
            response.setMessage(message);
            planResults.put(requestId, response);
        }
    }
    
    /**
     * Analyze weather windows and score them
     */
    private List<PlanResponse.WeatherWindow> analyzeWeatherWindows(List<Forecast> forecasts, PlanRequest request) {
        List<PlanResponse.WeatherWindow> windows = new ArrayList<>();
        
        // Group forecasts into 3-hour windows
        Map<OffsetDateTime, List<Forecast>> windowGroups = groupForecastsIntoWindows(forecasts);
        
        // Score each window
        for (Map.Entry<OffsetDateTime, List<Forecast>> entry : windowGroups.entrySet()) {
            OffsetDateTime windowStart = entry.getKey();
            List<Forecast> windowForecasts = entry.getValue();
            
            if (windowForecasts.isEmpty()) continue;
            
            // Calculate average conditions for the window
            double avgTemp = windowForecasts.stream().mapToDouble(f -> f.getTempC() != null ? f.getTempC() : 0).average().orElse(0);
            double avgPrecipProb = windowForecasts.stream().mapToDouble(f -> f.getPrecipProb() != null ? f.getPrecipProb() : 0).average().orElse(0);
            double avgWind = windowForecasts.stream().mapToDouble(f -> f.getWindMps() != null ? f.getWindMps() : 0).average().orElse(0);
            double avgUv = windowForecasts.stream().mapToDouble(f -> f.getUvIndex() != null ? f.getUvIndex() : 0).average().orElse(0);
            
            // Score the window
            double score = calculateWindowScore(avgTemp, avgPrecipProb, avgWind, avgUv, request.getPrefs());
            String explanation = generateExplanation(avgTemp, avgPrecipProb, avgWind, avgUv, request.getPrefs());
            
            // Create weather window
            PlanResponse.WeatherWindow window = new PlanResponse.WeatherWindow();
            window.setStartTime(windowStart);
            window.setEndTime(windowStart.plusHours(3));
            window.setScore(score);
            window.setExplanation(explanation);
            
            PlanResponse.WeatherWindow.WeatherConditions conditions = new PlanResponse.WeatherWindow.WeatherConditions();
            conditions.setTempC(avgTemp);
            conditions.setPrecipProb(avgPrecipProb);
            conditions.setWindMps(avgWind);
            conditions.setUvIndex(avgUv);
            window.setConditions(conditions);
            
            windows.add(window);
        }
        
        // Sort by score (highest first) and return top 5
        return windows.stream()
            .sorted((w1, w2) -> Double.compare(w2.getScore(), w1.getScore()))
            .limit(5)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Group forecasts into 3-hour windows
     */
    private Map<OffsetDateTime, List<Forecast>> groupForecastsIntoWindows(List<Forecast> forecasts) {
        Map<OffsetDateTime, List<Forecast>> groups = new HashMap<>();
        
        for (Forecast forecast : forecasts) {
            // Round down to nearest 3-hour boundary
            OffsetDateTime windowStart = forecast.getHourUtc()
                .truncatedTo(ChronoUnit.HOURS)
                .withHour((forecast.getHourUtc().getHour() / 3) * 3);
            
            groups.computeIfAbsent(windowStart, k -> new ArrayList<>()).add(forecast);
        }
        
        return groups;
    }
    
    /**
     * Calculate score for a weather window
     */
    private double calculateWindowScore(double temp, double precipProb, double wind, double uv, PlanRequest.PlanPreferences prefs) {
        double score = 100.0; // Start with perfect score
        
        // Temperature penalty
        if (temp < prefs.getMinTempC() || temp > prefs.getMaxTempC()) {
            double tempPenalty = Math.min(Math.abs(temp - prefs.getMinTempC()), Math.abs(temp - prefs.getMaxTempC()));
            score -= tempPenalty * 2;
        }
        
        // Precipitation penalty (high impact)
        if (precipProb > prefs.getMaxPrecipProb()) {
            score -= (precipProb - prefs.getMaxPrecipProb()) * 50;
        }
        
        // Wind penalty
        if (wind > prefs.getMaxWindMps()) {
            score -= (wind - prefs.getMaxWindMps()) * 5;
        }
        
        // UV penalty
        if (uv > prefs.getMaxUvIndex()) {
            score -= (uv - prefs.getMaxUvIndex()) * 3;
        }
        
        return Math.max(0, score);
    }
    
    /**
     * Generate explanation for the score
     */
    private String generateExplanation(double temp, double precipProb, double wind, double uv, PlanRequest.PlanPreferences prefs) {
        List<String> factors = new ArrayList<>();
        
        if (temp < prefs.getMinTempC() || temp > prefs.getMaxTempC()) {
            factors.add(String.format("Temperature %.1f°C outside ideal range (%.1f-%.1f°C)", temp, prefs.getMinTempC(), prefs.getMaxTempC()));
        }
        
        if (precipProb > prefs.getMaxPrecipProb()) {
            factors.add(String.format("High rain probability %.0f%%", precipProb * 100));
        }
        
        if (wind > prefs.getMaxWindMps()) {
            factors.add(String.format("Strong winds %.1f m/s", wind));
        }
        
        if (uv > prefs.getMaxUvIndex()) {
            factors.add(String.format("High UV index %.1f", uv));
        }
        
        if (factors.isEmpty()) {
            return "Excellent conditions for outdoor activities";
        }
        
        return "Consider: " + String.join(", ", factors);
    }
}
