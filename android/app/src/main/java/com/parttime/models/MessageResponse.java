package com.parttime.models;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("senderId")
    private Long senderId;
    
    @SerializedName("senderName")
    private String senderName;
    
    @SerializedName("receiverId")
    private Long receiverId;
    
    @SerializedName("receiverName")
    private String receiverName;
    
    @SerializedName("jobId")
    private Long jobId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("timestamp")
    private String timestamp;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}


