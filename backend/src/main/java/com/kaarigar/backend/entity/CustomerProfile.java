package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_profiles")
@Data
@NoArgsConstructor
public class CustomerProfile {

    @Id
    private String userId;

    @OneToOne
    @MapsId // Concept: Shared Primary Key - The Customer ID is the same as the User ID
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    private String phoneNumber;
    private String address;
    private String city;
    private String pincode;
}