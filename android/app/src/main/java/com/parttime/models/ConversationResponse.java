package com.parttime.models;

import com.google.gson.annotations.SerializedName;

public class ConversationResponse {
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("userName")
    private String userName;
    
    @SerializedName("lastMessage")
    private String lastMessage;
    
    @SerializedName("lastMessageTime")
    private String lastMessageTime;
    
    @SerializedName("jobId")
    private Long jobId;
    
    @SerializedName("jobTitle")
    private String jobTitle;
    
    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}


