package com.outscout.api.repo;

import com.outscout.api.model.entity.Campsite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Campsite entities
 */
@Repository
public interface CampsiteRepository extends JpaRepository<Campsite, Long> {
    
    /**
     * Find campsites by park
     */
    List<Campsite> findByParkId(Long parkId);
    
    /**
     * Search campsites with filters
     */
    @Query("SELECT c FROM Campsite c " +
           "LEFT JOIN c.park p " +
           "LEFT JOIN c.amenities a " +
           "WHERE (:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "       OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:region IS NULL OR p.region = :region) " +
           "AND (:petAllowed IS NULL OR c.petAllowed = :petAllowed) " +
           "AND (:bookable IS NULL OR c.bookable = :bookable) " +
           "AND (:hasBbq IS NULL OR a.hasBbq = :hasBbq) " +
           "AND (:hasToilet IS NULL OR a.hasToilet = :hasToilet) " +
           "AND (:hasWater IS NULL OR a.hasWater = :hasWater) " +
           "AND (:hasShelter IS NULL OR a.hasShelter = :hasShelter) " +
           "AND (:hasPower IS NULL OR a.hasPower = :hasPower)")
    Page<Campsite> searchCampsites(@Param("query") String query,
                                  @Param("region") String region,
                                  @Param("petAllowed") Boolean petAllowed,
                                  @Param("bookable") Boolean bookable,
                                  @Param("hasBbq") Boolean hasBbq,
                                  @Param("hasToilet") Boolean hasToilet,
                                  @Param("hasWater") Boolean hasWater,
                                  @Param("hasShelter") Boolean hasShelter,
                                  @Param("hasPower") Boolean hasPower,
                                  Pageable pageable);
    
    /**
     * Find campsites within a geographic bounding box
     */
    @Query("SELECT c FROM Campsite c WHERE " +
           "c.lat BETWEEN :minLat AND :maxLat AND " +
           "c.lon BETWEEN :minLon AND :maxLon")
    List<Campsite> findByLocationWithin(@Param("minLat") BigDecimal minLat,
                                       @Param("maxLat") BigDecimal maxLat,
                                       @Param("minLon") BigDecimal minLon,
                                       @Param("maxLon") BigDecimal maxLon);
    
    /**
     * Find campsite by name and park
     */
    Optional<Campsite> findByNameAndParkId(String name, Long parkId);
}
