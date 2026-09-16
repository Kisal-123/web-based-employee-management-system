package com.emp.management.leaveattendance.repository;

import com.emp.management.leaveattendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Finds all leave requests for a specific employee (for the Employee Dashboard)
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    // Finds all leave requests by status (so the Manager can filter just the "PENDING" ones)
    List<LeaveRequest> findByStatus(String status);
}