package com.parttime.models;

public class MessageRequest {
    private Long receiverId;
    private Long jobId;
    private String content;
    
    public MessageRequest(Long receiverId, Long jobId, String content) {
        this.receiverId = receiverId;
        this.jobId = jobId;
        this.content = content;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}


