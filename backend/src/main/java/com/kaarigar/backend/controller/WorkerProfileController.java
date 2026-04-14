package com.kaarigar.backend.controller;

import com.kaarigar.backend.dto.WorkerProfileDTO;
import com.kaarigar.backend.service.WorkerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Concept: REST Standard (Allows us to send status codes like 201 Created)
import org.springframework.security.core.context.SecurityContextHolder; // Concept: Security Context (Retrieves the UID we verified in the Filter)
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/worker")
public class WorkerProfileController {

    @Autowired
    private WorkerProfileService workerProfileService;

    @PostMapping("/submit")
    public ResponseEntity<?> createProfile(@RequestBody WorkerProfileDTO profileDTO) {
        // String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid = "test_uid_123"; // Hardcoded for "College Project" testing mode

        workerProfileService.saveWorkerProfile(uid, profileDTO);
        return ResponseEntity.ok("Profile created successfully!");
    }

    @GetMapping("/my-profile")
    public ResponseEntity<WorkerProfileDTO> getMyProfile() {
        // Concept: Principal Retrieval
        // In a real app, we get the UID from the Security Guard (Filter)
        String uid = "test_uid_123"; // Still hardcoded for our "Backdoor" test

        WorkerProfileDTO profile = workerProfileService.getWorkerProfile(uid);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update") // Concept: PUT Method for Updates
    public ResponseEntity<?> updateProfile(@jakarta.validation.Valid @RequestBody WorkerProfileDTO profileDTO) {
        String uid = "test_uid_123"; // Still using our testing UID

        workerProfileService.updateWorkerProfile(uid, profileDTO);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<WorkerProfileDTO>> search(
            @RequestParam String occupation,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "10.0") Double radius) {

        // Concept: Default Parameters
        // If the user doesn't specify a radius, we default to 10km.

        return ResponseEntity.ok(workerProfileService.searchWorkers(occupation, lat, lon, radius));
    }
}