package com.outscout.api.repo;

import com.outscout.api.model.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Park entities
 */
@Repository
public interface ParkRepository extends JpaRepository<Park, Long> {
    
    /**
     * Find parks by region
     */
    List<Park> findByRegion(String region);
    
    /**
     * Find park by name (case-insensitive)
     */
    Optional<Park> findByNameIgnoreCase(String name);
    
    /**
     * Search parks by name containing text (case-insensitive)
     */
    @Query("SELECT p FROM Park p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Park> findByNameContainingIgnoreCase(@Param("query") String query);
    
    /**
     * Find parks by authority
     */
    List<Park> findByAuthority(String authority);
}
