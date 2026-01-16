package com.parttime.models;

public class ReviewRequest {
    private Long revieweeId;
    private Long jobId;
    private Integer rating;
    private String comment;
    
    public ReviewRequest(Long revieweeId, Long jobId, Integer rating, String comment) {
        this.revieweeId = revieweeId;
        this.jobId = jobId;
        this.rating = rating;
        this.comment = comment;
    }
    
    public Long getRevieweeId() {
        return revieweeId;
    }
    
    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}


