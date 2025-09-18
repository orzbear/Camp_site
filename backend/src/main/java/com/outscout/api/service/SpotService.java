package com.outscout.api.service;

import com.outscout.api.model.dto.SpotResponse;
import com.outscout.api.model.dto.SpotSearchRequest;
import com.outscout.api.model.entity.Campsite;
import com.outscout.api.repo.CampsiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for campsite operations
 */
@Service
@Transactional(readOnly = true)
public class SpotService {
    
    private static final Logger logger = LoggerFactory.getLogger(SpotService.class);
    
    @Autowired
    private CampsiteRepository campsiteRepository;
    
    /**
     * Search campsites with filters and pagination
     */
    @Cacheable(value = "spots", key = "#request.toString()", condition = "#result != null")
    public Page<SpotResponse> searchSpots(SpotSearchRequest request) {
        logger.debug("Searching spots with request: {}", request);
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Campsite> campsites = campsiteRepository.searchCampsites(
            request.getQuery(),
            request.getRegion(),
            request.getPetAllowed(),
            request.getBookable(),
            request.getHasBbq(),
            request.getHasToilet(),
            request.getHasWater(),
            request.getHasShelter(),
            request.getHasPower(),
            pageable
        );
        
        return campsites.map(this::mapToSpotResponse);
    }
    
    /**
     * Get campsite by ID
     */
    public Optional<SpotResponse> getSpotById(Long id) {
        logger.debug("Getting spot by ID: {}", id);
        
        return campsiteRepository.findById(id)
            .map(this::mapToSpotResponse);
    }
    
    /**
     * Map Campsite entity to SpotResponse DTO
     */
    private SpotResponse mapToSpotResponse(Campsite campsite) {
        SpotResponse response = new SpotResponse();
        response.setId(campsite.getId());
        response.setName(campsite.getName());
        response.setLat(campsite.getLat());
        response.setLon(campsite.getLon());
        response.setDescription(campsite.getDescription());
        response.setFeeAud(campsite.getFeeAud());
        response.setPetAllowed(campsite.getPetAllowed());
        response.setBookable(campsite.getBookable());
        response.setCreatedAt(campsite.getCreatedAt());
        response.setUpdatedAt(campsite.getUpdatedAt());
        
        // Map park info
        if (campsite.getPark() != null) {
            SpotResponse.ParkInfo parkInfo = new SpotResponse.ParkInfo(
                campsite.getPark().getId(),
                campsite.getPark().getName(),
                campsite.getPark().getRegion(),
                campsite.getPark().getAuthority(),
                campsite.getPark().getWebsiteUrl()
            );
            response.setPark(parkInfo);
        }
        
        // Map amenities info
        if (campsite.getAmenities() != null) {
            SpotResponse.AmenitiesInfo amenitiesInfo = new SpotResponse.AmenitiesInfo(
                campsite.getAmenities().getHasBbq(),
                campsite.getAmenities().getHasToilet(),
                campsite.getAmenities().getHasWater(),
                campsite.getAmenities().getHasShelter(),
                campsite.getAmenities().getHasPower()
            );
            response.setAmenities(amenitiesInfo);
        }
        
        return response;
    }
}
