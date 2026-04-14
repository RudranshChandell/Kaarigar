package com.kaarigar.backend.service;

import com.kaarigar.backend.dto.WorkerProfileDTO;
import com.kaarigar.backend.entity.Role;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.entity.WorkerProfile;
import com.kaarigar.backend.repository.ReviewRepository;
import com.kaarigar.backend.repository.UserRepository;
import com.kaarigar.backend.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired; // Concept: Dependency Injection (Spring handles the "new" keyword for you)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Concept: ACID (Atomic) - Ensures if one save fails, everything rolls back

import java.util.List;

@Service
public class WorkerProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Transactional
    public void updateWorkerProfile(String uid, WorkerProfileDTO dto) {
        // Concept: Fail-Fast Pattern
        // We check if the profile exists first. If not, we don't even try to update.
        WorkerProfile existingProfile = workerProfileRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Profile not found. Please create one first."));

        // Updating the fields
        existingProfile.setName(dto.getName());
        existingProfile.setOccupation(dto.getOccupation());
        existingProfile.setExperienceYears(dto.getExperienceYears());
        existingProfile.setPhone(dto.getPhone());
        existingProfile.setVideoUrl(dto.getVideoUrl());
        existingProfile.setPhotoUrl(dto.getPhotoUrl());
        existingProfile.setLatitude(dto.getLatitude());
        existingProfile.setLongitude(dto.getLongitude());

        // Concept: Dirty Checking
        // In JPA, you don't actually have to call .save() inside a @Transactional method
        // if the object is already "managed" (fetched from DB). Hibernate detects the changes
        // and saves them automatically when the method ends!
        workerProfileRepository.save(existingProfile);
    }

    public WorkerProfileDTO getWorkerProfile(String uid) {
        WorkerProfile profile = workerProfileRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("Profile not found for this worker"));

        // Concept: Manual Mapping (Entity -> DTO)
        // In a huge startup, we would use a tool like "MapStruct" to do this automatically.
        WorkerProfileDTO dto = new WorkerProfileDTO();
        dto.setName(profile.getName());
        dto.setOccupation(profile.getOccupation());
        dto.setExperienceYears(profile.getExperienceYears());
        dto.setVideoUrl(profile.getVideoUrl());
        dto.setPhotoUrl(profile.getPhotoUrl());
        dto.setPhone(profile.getPhone());
        dto.setLatitude(profile.getLatitude());
        dto.setLongitude(profile.getLongitude());

        return dto;
    }

    @Transactional
    public void saveWorkerProfile(String uid, WorkerProfileDTO dto) {
        // Concept: Exception Handling - We stop the code if something is wrong
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Concept: Business Rule Enforcement (Refencing "Identity Lock")
        if (user.getRole() != Role.WORKER) {
            throw new RuntimeException("Error: Only WORKER roles can create a professional profile.");
        }

        WorkerProfile profile = new WorkerProfile();
        // Concept: Mapping (DTO -> Entity)
        profile.setUser(user);
        profile.setName(dto.getName());
        profile.setOccupation(dto.getOccupation());
        profile.setExperienceYears(dto.getExperienceYears());
        profile.setVideoUrl(dto.getVideoUrl());
        profile.setPhotoUrl(dto.getPhotoUrl());
        profile.setPhone(dto.getPhone());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());

        workerProfileRepository.save(profile);

        // Concept: State Management
        user.setOnboardingComplete(true);
        userRepository.save(user);
    }

    public List<WorkerProfileDTO> searchWorkers(String occupation, Double lat, Double lon, Double radius) {
        List<WorkerProfile> workers = workerProfileRepository.findNearbyWorkers(occupation, lat, lon, radius);

        // Concept: Stream Mapping
        return workers.stream().map(worker -> {
            WorkerProfileDTO dto = new WorkerProfileDTO();
            dto.setName(worker.getName());
            dto.setOccupation(worker.getOccupation());
            dto.setExperienceYears(worker.getExperienceYears());
            dto.setPhone(worker.getPhone());
            dto.setPhotoUrl(worker.getPhotoUrl());
            dto.setLatitude(worker.getLatitude());
            dto.setLongitude(worker.getLongitude());
            return dto;
        }).toList();
    }

    @Transactional
    public void recalculateWorkerRating(String workerUid) {
        // 1. Get stats from the Review Repository
        Object[] stats = (Object[]) reviewRepository.getRatingStatsForWorker(workerUid);

        // 2. Extract values (Handle nulls if there are no reviews yet)
        Double avg = (stats[0] != null) ? (Double) stats[0] : 0.0;
        Long count = (stats[1] != null) ? (Long) stats[1] : 0L;

        // 3. Update the Worker Profile
        WorkerProfile profile = workerProfileRepository.findById(workerUid)
                .orElseThrow(() -> new RuntimeException("Worker Profile not found"));

        profile.setAverageRating(avg);
        profile.setTotalReviews(count.intValue());

        workerProfileRepository.save(profile);
    }
}