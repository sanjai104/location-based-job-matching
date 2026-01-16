package com.parttime.repository;

import com.parttime.model.Job;
import com.parttime.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByProviderId(Long providerId);
    List<Job> findByStatus(JobStatus status);
    
    @Query(value = "SELECT * FROM jobs j WHERE " +
            "(:minSalary IS NULL OR j.salary_max >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.salary_max <= :maxSalary) AND " +
            "(:jobType IS NULL OR j.job_type = :jobType) AND " +
            "j.status = 'ACTIVE'", nativeQuery = true)
    List<Job> findJobsWithFilters(@Param("minSalary") Double minSalary, 
                                  @Param("maxSalary") Double maxSalary,
                                  @Param("jobType") String jobType);
    
    @Query(value = "SELECT * FROM jobs j WHERE " +
            "j.status = 'ACTIVE' AND " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))", nativeQuery = true)
    List<Job> searchJobsByKeyword(@Param("keyword") String keyword);
}


