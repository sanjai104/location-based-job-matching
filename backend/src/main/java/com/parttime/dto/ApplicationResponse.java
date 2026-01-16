package com.parttime.dto;

import com.parttime.model.Application;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long studentId;
    private String studentName;
    private Application.ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String message;
}


