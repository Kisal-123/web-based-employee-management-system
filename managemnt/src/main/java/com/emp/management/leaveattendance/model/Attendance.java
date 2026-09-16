package com.emp.management.leaveattendance.model;

import com.emp.management.employeerecords.model.Employee;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_logs")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This creates a foreign key linking this log to a specific Employee!
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime clockInTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime clockOutTime;

    // We will calculate this automatically in the Service layer
    private Double overtimeHours = 0.0;

    // e.g., PRESENT, ABSENT, HALF_DAY
    private String status;

    // Constructors
    public Attendance() {}

    public Attendance(Employee employee, LocalDate workDate, LocalTime clockInTime) {
        this.employee = employee;
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.status = "PRESENT";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public LocalTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalTime clockInTime) { this.clockInTime = clockInTime; }

    public LocalTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalTime clockOutTime) { this.clockOutTime = clockOutTime; }

    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}