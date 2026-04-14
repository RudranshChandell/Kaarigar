package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "worker_profiles")
@Data
public class WorkerProfile {

    @Id
    private String userId; // This will be the same as the Firebase UID

    @OneToOne
    @MapsId // This tells JPA to use the User's ID as this table's Primary Key
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private String occupation;
    private int experienceYears;
    private String videoUrl; // The 10s verification video link
    private String photoUrl;
    private String phone;

    // We'll add PostGIS geometry later for the search, but for now:
    private Double latitude;
    private Double longitude;

    private Double averageRating = 0.0;
    private Integer totalReviews = 0;
}