package com.emp.management.leaveattendance.service;

import com.emp.management.leaveattendance.model.Attendance;
import com.emp.management.leaveattendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // 1. Fetch logs for a specific employee
    public List<Attendance> getAttendanceForEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    // 2. Clock In (Creates a new daily record)
    public Attendance recordClockIn(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    // 3. Clock Out & Automate Overtime Calculation
    public void recordClockOut(Long attendanceId, LocalTime clockOutTime) {
        // Find the specific attendance record for today
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Attendance ID"));

        attendance.setClockOutTime(clockOutTime);

        // Calculate the total time worked
        LocalTime clockIn = attendance.getClockInTime();
        Duration duration = Duration.between(clockIn, clockOutTime);

        // Convert the duration to total hours (e.g., 9.5 hours)
        double hoursWorked = duration.toMinutes() / 60.0;

        // Sri Lankan standard workday is 9 hours
        double standardHours = 9.0;

        if (hoursWorked > standardHours) {
            // Calculate OT and round it to 2 decimal places
            double overtime = hoursWorked - standardHours;
            attendance.setOvertimeHours(Math.round(overtime * 100.0) / 100.0);
        } else {
            attendance.setOvertimeHours(0.0);
        }

        // Save the updated record back to the database
        attendanceRepository.save(attendance);
    }
}