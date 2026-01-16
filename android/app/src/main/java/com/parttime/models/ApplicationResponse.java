package com.parttime.models;

import com.google.gson.annotations.SerializedName;

public class ApplicationResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("jobId")
    private Long jobId;
    
    @SerializedName("jobTitle")
    private String jobTitle;
    
    @SerializedName("studentId")
    private Long studentId;
    
    @SerializedName("studentName")
    private String studentName;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("appliedAt")
    private String appliedAt;
    
    @SerializedName("message")
    private String message;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}


