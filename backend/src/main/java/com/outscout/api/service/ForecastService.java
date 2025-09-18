package com.outscout.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outscout.api.model.entity.Forecast;
import com.outscout.api.repo.ForecastRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for weather forecast operations
 */
@Service
@Transactional
public class ForecastService {
    
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);
    
    @Autowired
    private ForecastRepository forecastRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Value("${app.weather.api-base}")
    private String weatherApiBase;
    
    @Value("${app.weather.cache-ttl}")
    private long cacheTtl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Get forecast for a specific location and time range
     */
    @Cacheable(value = "forecasts", key = "#lat + '_' + #lon + '_' + #fromTime + '_' + #toTime")
    public List<Forecast> getForecast(BigDecimal lat, BigDecimal lon, OffsetDateTime fromTime, OffsetDateTime toTime) {
        logger.debug("Getting forecast for lat={}, lon={}, from={}, to={}", lat, lon, fromTime, toTime);
        
        // Check if we have cached data
        List<Forecast> cachedForecasts = forecastRepository.findByLocationAndTimeRange(lat, lon, fromTime, toTime);
        
        if (!cachedForecasts.isEmpty()) {
            logger.debug("Found {} cached forecasts", cachedForecasts.size());
            return cachedForecasts;
        }
        
        // Fetch from external API
        return fetchAndCacheForecast(lat, lon, fromTime, toTime);
    }
    
    /**
     * Fetch forecast from Open-Meteo API and cache results
     */
    private List<Forecast> fetchAndCacheForecast(BigDecimal lat, BigDecimal lon, OffsetDateTime fromTime, OffsetDateTime toTime) {
        try {
            logger.info("Fetching forecast from Open-Meteo API for lat={}, lon={}", lat, lon);
            
            String url = String.format("%s?latitude=%s&longitude=%s&hourly=temperature_2m,precipitation_probability,precipitation,wind_speed_10m,uv_index&start_date=%s&end_date=%s",
                weatherApiBase,
                lat,
                lon,
                fromTime.format(DateTimeFormatter.ISO_LOCAL_DATE),
                toTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
            );
            
            String response = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            return parseAndSaveForecast(lat, lon, response);
            
        } catch (Exception e) {
            logger.error("Error fetching forecast from API", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Parse Open-Meteo response and save to database
     */
    private List<Forecast> parseAndSaveForecast(BigDecimal lat, BigDecimal lon, String jsonResponse) {
        List<Forecast> forecasts = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode hourly = root.get("hourly");
            
            if (hourly == null) {
                logger.warn("No hourly data in API response");
                return forecasts;
            }
            
            JsonNode times = hourly.get("time");
            JsonNode temps = hourly.get("temperature_2m");
            JsonNode precipProbs = hourly.get("precipitation_probability");
            JsonNode precip = hourly.get("precipitation");
            JsonNode wind = hourly.get("wind_speed_10m");
            JsonNode uv = hourly.get("uv_index");
            
            for (int i = 0; i < times.size(); i++) {
                OffsetDateTime hourUtc = OffsetDateTime.parse(times.get(i).asText());
                
                // Check if forecast already exists
                if (forecastRepository.existsByLatAndLonAndHourUtc(lat, lon, hourUtc)) {
                    continue;
                }
                
                Forecast forecast = new Forecast();
                forecast.setLat(lat);
                forecast.setLon(lon);
                forecast.setHourUtc(hourUtc);
                forecast.setSource("open-meteo");
                
                if (temps != null && temps.get(i) != null && !temps.get(i).isNull()) {
                    forecast.setTempC(temps.get(i).asDouble());
                }
                
                if (precipProbs != null && precipProbs.get(i) != null && !precipProbs.get(i).isNull()) {
                    forecast.setPrecipProb(precipProbs.get(i).asDouble() / 100.0); // Convert percentage to decimal
                }
                
                if (precip != null && precip.get(i) != null && !precip.get(i).isNull()) {
                    forecast.setPrecipMm(precip.get(i).asDouble());
                }
                
                if (wind != null && wind.get(i) != null && !wind.get(i).isNull()) {
                    forecast.setWindMps(wind.get(i).asDouble());
                }
                
                if (uv != null && uv.get(i) != null && !uv.get(i).isNull()) {
                    forecast.setUvIndex(uv.get(i).asDouble());
                }
                
                forecasts.add(forecast);
            }
            
            // Save to database
            List<Forecast> savedForecasts = forecastRepository.saveAll(forecasts);
            logger.info("Saved {} new forecasts to database", savedForecasts.size());
            
            return savedForecasts;
            
        } catch (Exception e) {
            logger.error("Error parsing forecast response", e);
            return forecasts;
        }
    }
    
    /**
     * Clean up old forecasts
     */
    @Transactional
    public void cleanupOldForecasts() {
        OffsetDateTime cutoffTime = OffsetDateTime.now().minusHours(48);
        forecastRepository.deleteOldForecasts(cutoffTime);
        logger.info("Cleaned up forecasts older than {}", cutoffTime);
    }
}
