package com.kaarigar.backend.service;

import com.kaarigar.backend.entity.JobRequest;
import com.kaarigar.backend.entity.JobStatus;
import com.kaarigar.backend.entity.Rating;
import com.kaarigar.backend.exception.MarketplaceException;
import com.kaarigar.backend.repository.JobRequestRepository;
import com.kaarigar.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private JobRequestRepository jobRepo;
    @Transactional
    public void postReview(Long jobId, int rating, String comment) {
        // 1. Fetch the JobRequest
        JobRequest jobRequest = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // 2. State Check: Only completed jobs can be rated
        if (jobRequest.getStatus() != JobStatus.COMPLETED) {
            // 🛡️ Change this:
            throw new MarketplaceException("You can only review a job that is COMPLETED.");
        }

        // 3. Create a new Rating object (Using the entity name you created)
        Rating jobRating = new Rating();

        // 4. Link the JobRequest to the Rating
        // 🛡️ Concept: @MapsId Magic
        // Because of @MapsId in your Entity, this line automatically
        // sets the ID of 'jobRating' to match 'jobId'.
        jobRating.setJobRequest(jobRequest);

        // 5. Set the rating and comment
        // Startup Tip: You might want to add a check here: if (rating < 1 || rating > 5)
        jobRating.setRating(rating);
        jobRating.setComment(comment);

        // 6. Save the review
        reviewRepo.save(jobRating);
    }
}