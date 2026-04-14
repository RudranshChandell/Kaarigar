package com.kaarigar.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="job_ratings")
@Data
@NoArgsConstructor
public class Rating {

    @Id
    private Long jobId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "jobId")
    private JobRequest jobRequest;


    int rating;

    String comment;
}
