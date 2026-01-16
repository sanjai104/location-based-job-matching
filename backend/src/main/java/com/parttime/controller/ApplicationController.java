package com.parttime.controller;

import com.parttime.dto.ApplicationRequest;
import com.parttime.dto.ApplicationResponse;
import com.parttime.model.Application;
import com.parttime.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        List<ApplicationResponse> applications = applicationService.getMyApplications();
        return ResponseEntity.ok(applications);
    }
    
    @GetMapping("/for-my-jobs")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForMyJobs() {
        List<ApplicationResponse> applications = applicationService.getApplicationsForMyJobs();
        return ResponseEntity.ok(applications);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam Application.ApplicationStatus status) {
        ApplicationResponse response = applicationService.updateApplicationStatus(id, status);
        return ResponseEntity.ok(response);
    }
}


