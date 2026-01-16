package com.parttime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long userId;
    private String userName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long jobId;
    private String jobTitle;
}


