package com.kaarigar.backend.service;

import com.kaarigar.backend.entity.JobRequest;
import com.kaarigar.backend.entity.JobStatus;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.repository.JobRequestRepository;
import com.kaarigar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobRequestService {

    @Autowired
    private JobRequestRepository jobRepo;
    @Autowired
    private UserRepository userRepo;

    @Transactional
    public void createRequest(String customerUid, String workerUid, String description) {
        User customer = userRepo.findById(customerUid)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User worker = userRepo.findById(workerUid)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        JobRequest request = new JobRequest();
        request.setCustomer(customer);
        request.setWorker(worker);
        request.setServiceDescription(description);
        request.setStatus(JobStatus.PENDING);

        jobRepo.save(request);
    }

    public List<JobRequest> getWorkerInbox(String workerUid) {
        User worker = userRepo.findById(workerUid)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        // Concept: Data Retrieval Order
        return jobRepo.findByWorkerOrderByCreatedAtDesc(worker);
    }

    @Transactional
    public void updateJobStatus(Long jobId, JobStatus newStatus) {
        JobRequest request = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job Request not found"));

        // 🛡️ Concept: Business Rule Validation
        if (request.getStatus() != JobStatus.PENDING) {
            throw new RuntimeException("Decision already made. Current status: " + request.getStatus());
        }

        request.setStatus(newStatus);
        jobRepo.save(request);
    }
    @Transactional
    public void completeJob(Long jobId) {
        JobRequest request = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Guard: Can't complete a job that wasn't accepted!
        if (request.getStatus() != JobStatus.ACCEPTED) {
            throw new RuntimeException("Cannot complete a job that is currently: " + request.getStatus());
        }

        request.setStatus(JobStatus.COMPLETED);
        jobRepo.save(request);
    }
    public List<JobRequest> getCustomerHistory(String customerUid) {
        User customer = userRepo.findById(customerUid)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return jobRepo.findByCustomerOrderByCreatedAtDesc(customer);
    }
}