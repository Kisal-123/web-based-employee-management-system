package com.emp.management.leaveattendance.repository;

import com.emp.management.leaveattendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // This allows the Operations Manager to find all attendance logs for a specific employee
    List<Attendance> findByEmployeeId(Long employeeId);
}