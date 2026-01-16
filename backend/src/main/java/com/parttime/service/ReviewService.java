package com.parttime.service;

import com.parttime.dto.ReviewRequest;
import com.parttime.dto.ReviewResponse;
import com.parttime.model.Job;
import com.parttime.model.Review;
import com.parttime.model.User;
import com.parttime.repository.JobRepository;
import com.parttime.repository.ReviewRepository;
import com.parttime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    public ReviewResponse createReview(ReviewRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User reviewer = (User) auth.getPrincipal();
        
        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new RuntimeException("Reviewee not found"));
        
        if (reviewer.getId().equals(reviewee.getId())) {
            throw new RuntimeException("Cannot review yourself");
        }
        
        Job job = null;
        if (request.getJobId() != null) {
            job = jobRepository.findById(request.getJobId())
                    .orElse(null);
        }
        
        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setJob(job);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        review = reviewRepository.save(review);
        return convertToResponse(review);
    }
    
    public List<ReviewResponse> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByRevieweeId(userId);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public Double getAverageRating(Long userId) {
        return reviewRepository.getAverageRating(userId);
    }
    
    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setReviewerId(review.getReviewer().getId());
        response.setReviewerName(review.getReviewer().getName());
        response.setRevieweeId(review.getReviewee().getId());
        response.setRevieweeName(review.getReviewee().getName());
        if (review.getJob() != null) {
            response.setJobId(review.getJob().getId());
            response.setJobTitle(review.getJob().getTitle());
        }
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}


