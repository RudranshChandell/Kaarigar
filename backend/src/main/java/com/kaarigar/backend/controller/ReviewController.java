package com.kaarigar.backend.controller;

import com.kaarigar.backend.dto.ReviewRequestDTO;
import com.kaarigar.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequestDTO request) {
        // We pass the data from the DTO directly to our Service
        reviewService.postReview(
                request.getJobId(),
                request.getRating(),
                request.getComment()
        );

        return ResponseEntity.ok("Review submitted! Thank you for your feedback.");
    }
}