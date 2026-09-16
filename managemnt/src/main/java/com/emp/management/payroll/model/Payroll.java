package com.emp.management.payroll.model;

import com.emp.management.employeerecords.model.Employee;
import jakarta.persistence.*;

@Entity
@Table(name = "payroll_records")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // The month and year this payslip is for (e.g., "September 2026")
    private String payPeriod;

    // Financial Breakdown
    private Double basicSalary;
    private Double overtimePay;
    private Double allowances = 0.0;
    private Double grossSalary;

    // Statutory Deductions (Sri Lanka)
    private Double epfDeduction; // 8% from Employee
    private Double employerEpf;  // 12% from Employer (Company Cost)
    private Double employerEtf;  // 3% from Employer (Company Cost)
    private Double apitTax = 0.0; // Advance Personal Income Tax

    private Double deductions = 0.0; // Manual deductions and Unpaid Leave penalties

    private Double netSalary; // The final take-home pay

    // Default constructor
    public Payroll() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }

    public Double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(Double overtimePay) { this.overtimePay = overtimePay; }

    public Double getAllowances() { return allowances; }
    public void setAllowances(Double allowances) { this.allowances = allowances; }

    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }

    public Double getEpfDeduction() { return epfDeduction; }
    public void setEpfDeduction(Double epfDeduction) { this.epfDeduction = epfDeduction; }

    public Double getEmployerEpf() { return employerEpf; }
    public void setEmployerEpf(Double employerEpf) { this.employerEpf = employerEpf; }

    public Double getEmployerEtf() { return employerEtf; }
    public void setEmployerEtf(Double employerEtf) { this.employerEtf = employerEtf; }

    public Double getApitTax() { return apitTax; }
    public void setApitTax(Double apitTax) { this.apitTax = apitTax; }

    public Double getDeductions() { return deductions; }
    public void setDeductions(Double deductions) { this.deductions = deductions; }

    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
}