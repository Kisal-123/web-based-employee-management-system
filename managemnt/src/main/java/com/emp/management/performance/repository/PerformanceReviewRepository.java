package com.emp.management.performance.repository;

import com.emp.management.performance.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    // Custom query to find all reviews for a specific employee
    List<PerformanceReview> findByEmployeeId(Long employeeId);
    PerformanceReview findTopByEmployeeIdOrderByIdDesc(Long employeeId);
}