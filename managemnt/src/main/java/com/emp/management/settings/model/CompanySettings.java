package com.emp.management.settings.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_settings")
public class CompanySettings {

    // We hardcode the ID to 1 because there will only ever be ONE row of settings
    @Id
    private Long id = 1L;

    // Statutory Tax Percentages
    private Double epfEmployeePercentage = 8.0;  // Default 8%
    private Double epfEmployerPercentage = 12.0; // Default 12%
    private Double etfEmployerPercentage = 3.0;  // Default 3%

    // Global Leave Limits
    private Integer annualLeaveLimit = 14;
    private Integer casualLeaveLimit = 7;
    private Integer medicalLeaveLimit = 14;

    public CompanySettings() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getEpfEmployeePercentage() { return epfEmployeePercentage; }
    public void setEpfEmployeePercentage(Double epfEmployeePercentage) { this.epfEmployeePercentage = epfEmployeePercentage; }

    public Double getEpfEmployerPercentage() { return epfEmployerPercentage; }
    public void setEpfEmployerPercentage(Double epfEmployerPercentage) { this.epfEmployerPercentage = epfEmployerPercentage; }

    public Double getEtfEmployerPercentage() { return etfEmployerPercentage; }
    public void setEtfEmployerPercentage(Double etfEmployerPercentage) { this.epfEmployerPercentage = etfEmployerPercentage; }

    public Integer getAnnualLeaveLimit() { return annualLeaveLimit; }
    public void setAnnualLeaveLimit(Integer annualLeaveLimit) { this.annualLeaveLimit = annualLeaveLimit; }

    public Integer getCasualLeaveLimit() { return casualLeaveLimit; }
    public void setCasualLeaveLimit(Integer casualLeaveLimit) { this.casualLeaveLimit = casualLeaveLimit; }

    public Integer getMedicalLeaveLimit() { return medicalLeaveLimit; }
    public void setMedicalLeaveLimit(Integer medicalLeaveLimit) { this.medicalLeaveLimit = medicalLeaveLimit; }
}