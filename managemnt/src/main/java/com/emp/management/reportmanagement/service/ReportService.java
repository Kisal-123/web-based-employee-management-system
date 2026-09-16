package com.emp.management.reportmanagement.service;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.leaveattendance.model.Attendance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    // 1. Generate HR Employee Summary
    public byte[] generateEmployeeSummaryCsv(List<Employee> employees) {
        StringBuilder csv = new StringBuilder();
        // Create the Excel Header Row
        csv.append("Employee ID,First Name,Last Name,Email,Department,Designation,Hire Date,Status\n");

        // Loop through the database and add a row for each employee
        for (Employee emp : employees) {
            csv.append(emp.getId()).append(",")
                    .append(emp.getFirstName()).append(",")
                    .append(emp.getLastName()).append(",")
                    .append(emp.getEmail()).append(",")
                    .append(emp.getDepartment()).append(",")
                    .append(emp.getDesignation()).append(",")
                    .append(emp.getHireDate()).append(",")
                    .append(emp.getStatus()).append("\n");
        }
        return csv.toString().getBytes();
    }

    // 2. Generate Operations Attendance Summary
    public byte[] generateAttendanceSummaryCsv(List<Attendance> attendances) {
        StringBuilder csv = new StringBuilder();
        // Create the Excel Header Row
        csv.append("Work Date,Employee Name,Clock In,Clock Out,Overtime Hours\n");

        for (Attendance att : attendances) {
            csv.append(att.getWorkDate()).append(",")
                    .append(att.getEmployee().getFirstName()).append(" ").append(att.getEmployee().getLastName()).append(",")
                    .append(att.getClockInTime() != null ? att.getClockInTime() : "Pending").append(",")
                    .append(att.getClockOutTime() != null ? att.getClockOutTime() : "Pending").append(",")
                    .append(att.getOvertimeHours()).append("\n");
        }
        return csv.toString().getBytes();
    }
}