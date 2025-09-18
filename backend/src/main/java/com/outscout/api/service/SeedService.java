package com.outscout.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outscout.api.model.entity.*;
import com.outscout.api.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for seeding initial data (development only)
 */
@Service
public class SeedService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(SeedService.class);
    
    @Autowired
    private ParkRepository parkRepository;
    
    @Autowired
    private CampsiteRepository campsiteRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;
    
    @Value("${app.seed.data-path:classpath:seed}")
    private String dataPath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void run(String... args) throws Exception {
        if (!seedEnabled) {
            logger.info("Seed service is disabled");
            return;
        }
        
        logger.info("Starting seed data loading...");
        
        // Check if data already exists
        if (parkRepository.count() > 0) {
            logger.info("Data already exists, skipping seed loading");
            return;
        }
        
        try {
            loadParks();
            loadCampsites();
            loadAlerts();
            logger.info("Seed data loading completed successfully");
        } catch (Exception e) {
            logger.error("Error loading seed data", e);
        }
    }
    
    /**
     * Load parks from CSV
     */
    @Transactional
    private void loadParks() {
        logger.info("Loading parks...");
        
        try {
            Resource resource = new ClassPathResource("seed/parks.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String line;
            boolean firstLine = true;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    Park park = new Park();
                    park.setName(fields[0].trim());
                    park.setRegion(fields[1].trim());
                    park.setAuthority(fields[2].trim());
                    park.setWebsiteUrl(fields.length > 3 ? fields[3].trim() : null);
                    
                    parkRepository.save(park);
                    count++;
                }
            }
            
            reader.close();
            logger.info("Loaded {} parks", count);
            
        } catch (Exception e) {
            logger.error("Error loading parks", e);
        }
    }
    
    /**
     * Load campsites from CSV
     */
    @Transactional
    private void loadCampsites() {
        logger.info("Loading campsites...");
        
        try {
            Resource resource = new ClassPathResource("seed/campsites.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            
            String line;
            boolean firstLine = true;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] fields = line.split(",");
                if (fields.length >= 8) {
                    // Find park by name
                    Optional<Park> parkOpt = parkRepository.findByNameIgnoreCase(fields[0].trim());
                    if (parkOpt.isEmpty()) {
                        logger.warn("Park not found: {}", fields[0]);
                        continue;
                    }
                    
                    Park park = parkOpt.get();
                    
                    // Create campsite
                    Campsite campsite = new Campsite();
                    campsite.setPark(park);
                    campsite.setName(fields[1].trim());
                    campsite.setLat(new BigDecimal(fields[2].trim()));
                    campsite.setLon(new BigDecimal(fields[3].trim()));
                    campsite.setDescription(fields[4].trim());
                    campsite.setFeeAud(fields[5].trim().isEmpty() ? null : new BigDecimal(fields[5].trim()));
                    campsite.setPetAllowed(Boolean.parseBoolean(fields[6].trim()));
                    campsite.setBookable(Boolean.parseBoolean(fields[7].trim()));
                    
                    Campsite savedCampsite = campsiteRepository.save(campsite);
                    
                    // Create amenities if specified
                    if (fields.length > 8 && !fields[8].trim().isEmpty()) {
                        String amenitiesStr = fields[8].trim();
                        Amenities amenities = new Amenities();
                        amenities.setCampsite(savedCampsite);
                        amenities.setHasBbq(amenitiesStr.contains("BBQ"));
                        amenities.setHasToilet(amenitiesStr.contains("Toilets"));
                        amenities.setHasWater(amenitiesStr.contains("Water"));
                        amenities.setHasShelter(amenitiesStr.contains("Shelter"));
                        amenities.setHasPower(amenitiesStr.contains("Power"));
                        
                        // Save amenities (this will be handled by the repository)
                        savedCampsite.setAmenities(amenities);
                        campsiteRepository.save(savedCampsite);
                    }
                    
                    count++;
                }
            }
            
            reader.close();
            logger.info("Loaded {} campsites", count);
            
        } catch (Exception e) {
            logger.error("Error loading campsites", e);
        }
    }
    
    /**
     * Load alerts from JSON
     */
    @Transactional
    private void loadAlerts() {
        logger.info("Loading alerts...");
        
        try {
            Resource resource = new ClassPathResource("seed/alerts.json");
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            
            int count = 0;
            for (JsonNode alertNode : root) {
                String parkName = alertNode.get("park_name").asText();
                
                // Find park by name
                Optional<Park> parkOpt = parkRepository.findByNameIgnoreCase(parkName);
                if (parkOpt.isEmpty()) {
                    logger.warn("Park not found for alert: {}", parkName);
                    continue;
                }
                
                Park park = parkOpt.get();
                
                Alert alert = new Alert();
                alert.setPark(park);
                alert.setSeverity(alertNode.get("severity").asText());
                alert.setTitle(alertNode.get("title").asText());
                alert.setSummary(alertNode.has("summary") ? alertNode.get("summary").asText() : null);
                
                if (alertNode.has("starts_at")) {
                    alert.setStartsAt(OffsetDateTime.parse(alertNode.get("starts_at").asText()));
                }
                
                if (alertNode.has("ends_at")) {
                    alert.setEndsAt(OffsetDateTime.parse(alertNode.get("ends_at").asText()));
                }
                
                alert.setSource(alertNode.has("source") ? alertNode.get("source").asText() : "manual");
                alert.setUrl(alertNode.has("url") ? alertNode.get("url").asText() : null);
                
                alertRepository.save(alert);
                count++;
            }
            
            logger.info("Loaded {} alerts", count);
            
        } catch (Exception e) {
            logger.error("Error loading alerts", e);
        }
    }
}
