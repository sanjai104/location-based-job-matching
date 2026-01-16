package com.parttime.models;

import com.google.gson.annotations.SerializedName;

public class ReviewResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("reviewerId")
    private Long reviewerId;
    
    @SerializedName("reviewerName")
    private String reviewerName;
    
    @SerializedName("revieweeId")
    private Long revieweeId;
    
    @SerializedName("revieweeName")
    private String revieweeName;
    
    @SerializedName("jobId")
    private Long jobId;
    
    @SerializedName("rating")
    private Integer rating;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("jobTitle")
    private String jobTitle;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    
    public Long getRevieweeId() { return revieweeId; }
    public void setRevieweeId(Long revieweeId) { this.revieweeId = revieweeId; }
    
    public String getRevieweeName() { return revieweeName; }
    public void setRevieweeName(String revieweeName) { this.revieweeName = revieweeName; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}


