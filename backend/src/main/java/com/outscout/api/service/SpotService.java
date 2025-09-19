package com.outscout.api.service;

import com.outscout.api.model.dto.SpotResponse;
import com.outscout.api.model.dto.SpotSearchRequest;
import com.outscout.api.model.entity.Amenities;
import com.outscout.api.model.entity.Campsite;
import com.outscout.api.model.entity.Park;
import com.outscout.api.repo.CampsiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpotService {

    private static final Logger logger = LoggerFactory.getLogger(SpotService.class);

    private final CampsiteRepository campsiteRepository;

    public SpotService(CampsiteRepository campsiteRepository) {
        this.campsiteRepository = campsiteRepository;
    }

    public Page<SpotResponse> searchSpots(SpotSearchRequest request) {
        logger.debug("Searching spots with request: {}", request);

        int rawPage = request.getPage();
        int rawSize = request.getSize();

        int page = Math.max(rawPage, 0);
        int size = rawSize <= 0 ? 20 : Math.min(rawSize, 100);
        Pageable pageable = PageRequest.of(page, size);

        String q = normalize(request.getQuery());
        String region = normalize(request.getRegion());

        Page<Campsite> campsites = campsiteRepository.searchCampsites(
                q,
                region,
                request.getPetAllowed(),
                request.getBookable(),
                pageable
        );

        return campsites.map(this::mapToSpotResponse);
    }

    public java.util.Optional<SpotResponse> getSpotById(Long id) {
        logger.debug("Getting spot by ID: {}", id);
        return campsiteRepository.findById(id).map(this::mapToSpotResponse);
    }

    private SpotResponse mapToSpotResponse(Campsite c) {
        if (c == null) return null;
        SpotResponse dto = new SpotResponse();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setLat(c.getLat());
        dto.setLon(c.getLon());
        dto.setDescription(c.getDescription());
        dto.setFeeAud(c.getFeeAud());
        dto.setBookable(Boolean.TRUE.equals(c.getBookable()));
        dto.setPetAllowed(Boolean.TRUE.equals(c.getPetAllowed()));
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());

        Park p = c.getPark();
        if (p != null) {
            dto.setPark(new SpotResponse.ParkInfo(
                    p.getId(), p.getName(), p.getRegion(), p.getAuthority(), p.getWebsiteUrl()
            ));
        }

        Amenities a = c.getAmenities();
        if (a != null) {
            dto.setAmenities(new SpotResponse.AmenitiesInfo(
                    Boolean.TRUE.equals(a.getHasBbq()),
                    Boolean.TRUE.equals(a.getHasToilet()),
                    Boolean.TRUE.equals(a.getHasWater()),
                    Boolean.TRUE.equals(a.getHasShelter()),
                    Boolean.TRUE.equals(a.getHasPower())
            ));
        }
        return dto;
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
