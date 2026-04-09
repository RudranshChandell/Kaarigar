package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data // Generates Getters, Setters, toString, etc.
@NoArgsConstructor // Required by JPA
@AllArgsConstructor
public class User {

    @Id // MUST be jakarta.persistence.Id
    @Column(name = "firebase_uid")
    private String firebaseUid;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Role role;

    @Column(name = "onboarding_complete")
    private boolean onboardingComplete = false;
}