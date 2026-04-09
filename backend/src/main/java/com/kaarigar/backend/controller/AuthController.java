package com.kaarigar.backend.controller;

import com.kaarigar.backend.repository.UserRepository;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody Map<String, String> request) {
        String uid = request.get("uid");
        String email = request.get("email");
        String requestedRole = request.get("role"); // Optional: Passed only during first signup

        Optional<User> existingUser = userRepository.findById(uid);
        Map<String, Object> response = new HashMap<>();

        if (existingUser.isPresent()) {
            response.put("exists", true);
            response.put("role", existingUser.get().getRole());
            response.put("onboardingComplete", existingUser.get().isOnboardingComplete());
        } else {
            // New User Logic: Lock them into the role they chose
            User newUser = new User();
            newUser.setFirebaseUid(uid);
            newUser.setEmail(email);
            newUser.setRole(Role.valueOf(requestedRole)); // WORKER or CUSTOMER
            userRepository.save(newUser);

            response.put("exists", false);
            response.put("role", requestedRole);
        }

        return ResponseEntity.ok(response);
    }
}