package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_requests")
@Data
public class JobRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    private String serviceDescription;

    @Enumerated(EnumType.STRING) // 🛡️ Concept: Store the name "PENDING", not the index 0
    private JobStatus status;

    private LocalDateTime createdAt;

    @PrePersist // 🛡️ Concept: Automatically set the date before saving
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = JobStatus.PENDING;
    }
}