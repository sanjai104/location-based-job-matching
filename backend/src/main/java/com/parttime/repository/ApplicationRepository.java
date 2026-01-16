package com.parttime.repository;

import com.parttime.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByJobId(Long jobId);
    List<Application> findByJobProviderId(Long providerId);
    boolean existsByJobIdAndStudentId(Long jobId, Long studentId);
}


