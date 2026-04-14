package com.kaarigar.backend.repository;

import com.kaarigar.backend.entity.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, String> {

    // Concept: Haversine Formula
    // This is the mathematical formula used to calculate the distance between two points on a sphere.
    @Query(value = "SELECT * FROM worker_profiles w WHERE w.occupation = :occ " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(w.latitude)) * " +
            "cos(radians(w.longitude) - radians(:lon)) + sin(radians(:lat)) * " +
            "sin(radians(w.latitude)))) <= :dist", nativeQuery = true)
    List<WorkerProfile> findNearbyWorkers(@Param("occ") String occupation,
                                          @Param("lat") Double lat,
                                          @Param("lon") Double lon,
                                          @Param("dist") Double distance);
}