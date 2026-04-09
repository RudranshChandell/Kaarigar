package com.kaarigar.backend.repository;

import com.kaarigar.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // This allows you to find a user by their Firebase Email if needed
    Optional<User> findByEmail(String email);

    // Check if a UID already exists
    boolean existsByFirebaseUid(String firebaseUid);
}