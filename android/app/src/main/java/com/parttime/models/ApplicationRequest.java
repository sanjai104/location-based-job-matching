package com.parttime.models;

public class ApplicationRequest {
    private Long jobId;
    private String message;
    
    public ApplicationRequest(Long jobId, String message) {
        this.jobId = jobId;
        this.message = message;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}


