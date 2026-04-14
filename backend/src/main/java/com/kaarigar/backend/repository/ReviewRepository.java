package com.kaarigar.backend.repository;

import com.kaarigar.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Rating, Long> {
    // 🛡️ Concept: JPQL Aggregation
    // This query calculates the average and count across all jobs belonging to a worker
    @Query("SELECT AVG(r.rating), COUNT(r) FROM Rating r WHERE r.jobRequest.worker.uid = :workerUid")
    Object[] getRatingStatsForWorker(@Param("workerUid") String workerUid);
}