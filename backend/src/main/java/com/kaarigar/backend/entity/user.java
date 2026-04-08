package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "users")
public class user {
    @Id
    private String firebaseUid; // The ID from Google/Firebase

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Role role; // WORKER or CUSTOMER (Locked forever)

    private boolean onboardingComplete = false;
}

public enum Role { WORKER, CUSTOMER }
