package com.kaarigar.backend.repository;

import com.kaarigar.backend.entity.JobRequest;
import com.kaarigar.backend.entity.JobStatus;
import com.kaarigar.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {

    // Concept: Derived Query Methods
    // Spring generates the SQL automatically based on the method name!

    // Find all jobs sent to a specific worker (for the Worker Dashboard)
    List<JobRequest> findByWorkerOrderByCreatedAtDesc(User worker);

    // Find all jobs booked by a specific customer (for the Customer History)
    List<JobRequest> findByCustomerOrderByCreatedAtDesc(User customer);

    // Find jobs by status (e.g., show all 'PENDING' jobs for a worker)
    List<JobRequest> findByWorkerAndStatus(User worker, JobStatus status);
}