package com.parttime.service;

import com.parttime.dto.ApplicationRequest;
import com.parttime.dto.ApplicationResponse;
import com.parttime.model.Application;
import com.parttime.model.Job;
import com.parttime.model.User;
import com.parttime.repository.ApplicationRepository;
import com.parttime.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    public ApplicationResponse createApplication(ApplicationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (user.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("Only students can apply to jobs");
        }
        
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        if (job.getStatus() != Job.JobStatus.ACTIVE) {
            throw new RuntimeException("Cannot apply to inactive job");
        }
        
        if (applicationRepository.existsByJobIdAndStudentId(request.getJobId(), user.getId())) {
            throw new RuntimeException("You have already applied to this job");
        }
        
        Application application = new Application();
        application.setJob(job);
        application.setStudent(user);
        application.setMessage(request.getMessage());
        application.setStatus(Application.ApplicationStatus.PENDING);
        
        application = applicationRepository.save(application);
        return convertToResponse(application);
    }
    
    public List<ApplicationResponse> getMyApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        return applicationRepository.findByStudentId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ApplicationResponse> getApplicationsForMyJobs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (user.getRole() != User.Role.PROVIDER) {
            throw new RuntimeException("Only providers can view applications for their jobs");
        }
        
        return applicationRepository.findByJobProviderId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ApplicationResponse updateApplicationStatus(Long id, Application.ApplicationStatus status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (user.getRole() != User.Role.PROVIDER) {
            throw new RuntimeException("Only providers can update application status");
        }
        
        if (!application.getJob().getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update applications for your own jobs");
        }
        
        application.setStatus(status);
        application = applicationRepository.save(application);
        return convertToResponse(application);
    }
    
    private ApplicationResponse convertToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setJobId(application.getJob().getId());
        response.setJobTitle(application.getJob().getTitle());
        response.setStudentId(application.getStudent().getId());
        response.setStudentName(application.getStudent().getName());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        response.setMessage(application.getMessage());
        return response;
    }
}


