package com.emp.management.performance.model;

import com.emp.management.employeerecords.model.Employee;
import jakarta.persistence.*;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private String reviewPeriod; // e.g., "2026 - Q3"

    @Column(columnDefinition = "TEXT")
    private String kpiGoals; // The targets set by the manager

    private Integer rating; // 1 to 5 scale

    private Double bonusPercentage; // Automatically calculated based on the rating

    private String managerRemarks;

    // Default constructor
    public PerformanceReview() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }

    public String getKpiGoals() { return kpiGoals; }
    public void setKpiGoals(String kpiGoals) { this.kpiGoals = kpiGoals; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Double getBonusPercentage() { return bonusPercentage; }
    public void setBonusPercentage(Double bonusPercentage) { this.bonusPercentage = bonusPercentage; }

    public String getManagerRemarks() { return managerRemarks; }
    public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
}