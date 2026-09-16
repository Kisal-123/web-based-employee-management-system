package com.emp.management.leaveattendance.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.service.EmployeeService;
import com.emp.management.leaveattendance.model.Attendance;
import com.emp.management.leaveattendance.repository.AttendanceRepository;
import com.emp.management.leaveattendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.emp.management.reportmanagement.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository; // For fetching all logs

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportService reportService;

    // 1. View all attendance logs for the Operations Manager
    @GetMapping("/attendance")
    public String viewAttendanceLogs(Model model) {
        // Fetch all attendance records to display in the table
        model.addAttribute("attendances", attendanceRepository.findAll());
        // Fetch all active employees so we can select them in the Clock-In dropdown
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "attendance-list";
    }

    // --- NEW: ATTENDANCE SUMMARY EXPORT ---
    @GetMapping("/attendance/export")
    public ResponseEntity<byte[]> exportAttendanceReport() {
        byte[] csvData = reportService.generateAttendanceSummaryCsv(attendanceRepository.findAll());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Operations_Attendance_Summary.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return ResponseEntity.ok().headers(headers).body(csvData);
    }

    // 2. Clock In an employee
    @PostMapping("/attendance/clock-in")
    public String clockIn(@RequestParam("employeeId") Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        // Creates a new log with today's date and the exact current time
        Attendance attendance = new Attendance(employee, LocalDate.now(), LocalTime.now());
        attendanceService.recordClockIn(attendance);

        return "redirect:/attendance";
    }

    // 3. Clock Out and trigger the overtime calculation
    @PostMapping("/attendance/clock-out/{id}")
    public String clockOut(@PathVariable("id") Long id) {
        // Uses the exact current time to clock them out and calculate OT
        attendanceService.recordClockOut(id, LocalTime.now());

        return "redirect:/attendance";
    }
}