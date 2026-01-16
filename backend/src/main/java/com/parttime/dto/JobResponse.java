package com.parttime.dto;

import com.parttime.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private Long providerId;
    private String providerName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double salaryMin;
    private Double salaryMax;
    private Job.JobType jobType;
    private Job.JobStatus status;
    private String requirements;
    private LocalDateTime createdAt;
    private Double distance; // Distance in km from search location
}


