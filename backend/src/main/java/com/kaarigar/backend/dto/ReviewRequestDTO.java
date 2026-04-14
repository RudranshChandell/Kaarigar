package com.kaarigar.backend.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long jobId;
    private int rating;
    private String comment;
}