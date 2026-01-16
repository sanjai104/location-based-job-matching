package com.parttime.service;

import com.parttime.dto.JobRequest;
import com.parttime.dto.JobResponse;
import com.parttime.model.Job;
import com.parttime.model.User;
import com.parttime.repository.JobRepository;
import com.parttime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationService locationService;
    
    public JobResponse createJob(JobRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (user.getRole() != User.Role.PROVIDER) {
            throw new RuntimeException("Only providers can create jobs");
        }
        
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setProvider(user);
        job.setLatitude(request.getLatitude());
        job.setLongitude(request.getLongitude());
        job.setAddress(request.getAddress());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setJobType(request.getJobType());
        job.setRequirements(request.getRequirements());
        job.setStatus(Job.JobStatus.ACTIVE);
        
        job = jobRepository.save(job);
        return convertToResponse(job, null);
    }
    
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return convertToResponse(job, null);
    }
    
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(job -> convertToResponse(job, null))
                .collect(Collectors.toList());
    }
    
    public List<JobResponse> getJobsByProvider() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        return jobRepository.findByProviderId(user.getId()).stream()
                .map(job -> convertToResponse(job, null))
                .collect(Collectors.toList());
    }
    
    public List<JobResponse> searchJobs(Double lat, Double lng, Double radiusKm, 
                                       Double minSalary, Double maxSalary, 
                                       Job.JobType jobType, String keyword) {
        List<Job> jobs;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            jobs = jobRepository.searchJobsByKeyword(keyword);
        } else {
            String jobTypeStr = jobType != null ? jobType.name() : null;
            jobs = jobRepository.findJobsWithFilters(minSalary, maxSalary, jobTypeStr);
        }
        
        // Filter by location if provided
        if (lat != null && lng != null && radiusKm != null) {
            jobs = jobs.stream()
                    .filter(job -> job.getLatitude() != null && job.getLongitude() != null)
                    .filter(job -> locationService.isWithinRadius(
                            lat, lng, job.getLatitude(), job.getLongitude(), radiusKm))
                    .collect(Collectors.toList());
        }
        
        // Calculate distances and sort
        return jobs.stream()
                .map(job -> {
                    Double distance = null;
                    if (lat != null && lng != null && job.getLatitude() != null && job.getLongitude() != null) {
                        distance = locationService.calculateDistance(
                                lat, lng, job.getLatitude(), job.getLongitude());
                    }
                    return convertToResponse(job, distance);
                })
                .sorted((j1, j2) -> {
                    if (j1.getDistance() != null && j2.getDistance() != null) {
                        return Double.compare(j1.getDistance(), j2.getDistance());
                    }
                    return j2.getCreatedAt().compareTo(j1.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
    
    public JobResponse updateJob(Long id, JobRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own jobs");
        }
        
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLatitude(request.getLatitude());
        job.setLongitude(request.getLongitude());
        job.setAddress(request.getAddress());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setJobType(request.getJobType());
        job.setRequirements(request.getRequirements());
        
        job = jobRepository.save(job);
        return convertToResponse(job, null);
    }
    
    public void deleteJob(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (!job.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own jobs");
        }
        
        jobRepository.delete(job);
    }
    
    private JobResponse convertToResponse(Job job, Double distance) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setProviderId(job.getProvider().getId());
        response.setProviderName(job.getProvider().getName());
        response.setLatitude(job.getLatitude());
        response.setLongitude(job.getLongitude());
        response.setAddress(job.getAddress());
        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());
        response.setJobType(job.getJobType());
        response.setStatus(job.getStatus());
        response.setRequirements(job.getRequirements());
        response.setCreatedAt(job.getCreatedAt());
        response.setDistance(distance);
        return response;
    }
}


