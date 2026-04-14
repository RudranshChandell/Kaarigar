package com.kaarigar.backend.service;

import com.kaarigar.backend.dto.CustomerProfileDTO;
import com.kaarigar.backend.entity.CustomerProfile;
import com.kaarigar.backend.entity.Role;
import com.kaarigar.backend.entity.User;
import com.kaarigar.backend.repository.CustomerProfileRepository;
import com.kaarigar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerProfileRepository customerRepo;

    @Transactional
    public void createCustomerProfile(String uid, CustomerProfileDTO dto) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Identity mismatch: Only CUSTOMER roles can create a hirer profile.");
        }

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setAddress(dto.getAddress());
        profile.setCity(dto.getCity());
        profile.setPincode(dto.getPincode());

        customerRepo.save(profile);

        user.setOnboardingComplete(true);
        userRepository.save(user);
    }
}