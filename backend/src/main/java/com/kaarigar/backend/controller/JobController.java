package com.kaarigar.backend.controller;

import com.kaarigar.backend.entity.JobRequest;
import com.kaarigar.backend.entity.JobStatus;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.repository.JobRequestRepository;
import com.kaarigar.backend.repository.UserRepository;
import com.kaarigar.backend.service.JobRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRequestService jobService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRequestRepository jobRepo;

    @PostMapping("/book")
    public ResponseEntity<?> bookWorker(
            @RequestBody Map<String, String> payload,
            Authentication authentication) { // 🛡️ Spring injects the current user here!

        // 🛠 NO MORE HARDCODING!
        String customerUid = authentication.getName();

        String workerUid = payload.get("workerUid");
        String description = payload.get("description");

        jobService.createRequest(customerUid, workerUid, description);
        return ResponseEntity.ok("Job request sent!");
    }
    // 1. Get all requests for the logged-in worker
    @GetMapping("/worker/inbox")
    public ResponseEntity<List<JobRequest>> getInbox() {
        String workerUid = "test_uid_123"; // Simulating Rahul's login
        return ResponseEntity.ok(jobService.getWorkerInbox(workerUid));
    }

    // 2. Accept or Reject a specific job
    @PutMapping("/{jobId}/status")
    public ResponseEntity<?> respondToJob(
            @PathVariable Long jobId,
            @RequestParam JobStatus status) {

        jobService.updateJobStatus(jobId, status);
        return ResponseEntity.ok("Job status updated to: " + status);
    }

    @PutMapping("/{jobId}/complete")
    public ResponseEntity<?> finishJob(@PathVariable Long jobId) {
        jobService.completeJob(jobId);
        return ResponseEntity.ok("Job marked as COMPLETED. Time to get paid!");
    }

    @GetMapping("/customer/history")
    public ResponseEntity<List<JobRequest>> getCustomerHistory() {
        String customerUid = "new_customer_007"; // Simulating Amit's login
        // We need to fetch the User object first to pass it to the repo
        User customer = userRepository.findById(customerUid).get();

        return ResponseEntity.ok(jobRepo.findByCustomerOrderByCreatedAtDesc(customer));
    }
}