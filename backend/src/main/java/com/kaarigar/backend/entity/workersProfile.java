package com.kaarigar.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "workers_Profile")
public class workersProfile {

    @Id
    private String userId;

    private String occupation;
    private int experienceYears;
    private String videoUrl;
    private String photoUrl;
    private Point location;
}
