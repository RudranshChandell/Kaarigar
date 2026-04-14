package com.kaarigar.backend.controller;

import com.kaarigar.backend.dto.CustomerProfileDTO;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.repository.UserRepository;
import com.kaarigar.backend.service.CustomerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/profiles/customer")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService customerService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitProfile(@Valid @RequestBody CustomerProfileDTO dto) {
        // 🛡️ Concept: Hardcoding for Testing
        // We use this ID to simulate a logged-in user since we are in "Dev Mode"
        String uid = "new_customer_007";

        customerService.createCustomerProfile(uid, dto);
        return ResponseEntity.ok("Customer profile created!");
    }
}