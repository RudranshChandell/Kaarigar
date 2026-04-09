package com.kaarigar.backend.service;

import com.kaarigar.backend.entity.Role;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.entity.WorkerProfile;
import com.kaarigar.backend.repository.UserRepository;
import com.kaarigar.backend.repository.WorkerProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkerProfileRepository workerRepo;

    public void completeWorkerOnboarding(String uid, WorkerProfile profileData) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // IMMUTABLE ROLE CHECK: Safety first
        if (user.getRole() != Role.WORKER) {
            throw new RuntimeException("Identity mismatch: Only Workers can create a Career Record.");
        }

        profileData.setUser(user);
        workerRepo.save(profileData);

        user.setOnboardingComplete(true);
        userRepository.save(user);
    }
}