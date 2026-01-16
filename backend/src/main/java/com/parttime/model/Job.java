package com.parttime.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    private String address;
    
    @Column(name = "salary_min")
    private Double salaryMin;
    
    @Column(name = "salary_max")
    private Double salaryMax;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;
    
    private String requirements;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Application> applications;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Message> messages;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Review> reviews;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = JobStatus.ACTIVE;
        }
    }
    
    public enum JobType {
        PART_TIME, FLEXIBLE_HOURS, WEEKEND, EVENING
    }
    
    public enum JobStatus {
        ACTIVE, FILLED, CLOSED
    }
}


