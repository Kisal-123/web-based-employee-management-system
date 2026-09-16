package com.emp.management.leaveattendance.service;

import com.emp.management.leaveattendance.model.LeaveRequest;
import com.emp.management.leaveattendance.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    // Inject the Mail Sender we set up earlier!
    @Autowired
    private JavaMailSender mailSender;

    public LeaveRequest submitLeaveRequest(LeaveRequest request) {
        request.setStatus("PENDING");
        return leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    // Updated method to change status AND send email
    public void updateLeaveStatus(Long id, String status) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Leave Request ID: " + id));

        request.setStatus(status);
        leaveRequestRepository.save(request);

        // Trigger the email notification
        sendLeaveStatusEmail(request);
    }
    // --- NEW: AUTOMATION FOR PAYROLL ---
    public int processUnpaidLeaveForPayroll(Long employeeId) {
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeId(employeeId);
        int totalUnpaidDays = 0;

        for (LeaveRequest req : requests) {
            // Find approved leaves of type UNPAID
            if ("APPROVED".equals(req.getStatus()) && "UNPAID".equals(req.getLeaveType())) {

                // Calculate days between start and end date (+1 to include both the first and last day)
                long days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
                totalUnpaidDays += (int) days;

                // Mark as processed so it doesn't get deducted again next month!
                req.setStatus("PROCESSED_UNPAID");
                leaveRequestRepository.save(req);
            }
        }
        return totalUnpaidDays;
    }

    // Private method to construct and send the email
    private void sendLeaveStatusEmail(LeaveRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kisalranaabey03@gmail.com");
            message.setTo(request.getEmployee().getEmail()); // Grabs the employee's email from the database
            message.setSubject("Leave Request Update: " + request.getStatus());
            message.setText("Hello " + request.getEmployee().getFirstName() + ",\n\n" +
                    "Your leave request for the dates " + request.getStartDate() + " to " + request.getEndDate() +
                    " has been officially " + request.getStatus() + " by your manager.\n\n" +
                    "Please log into your SmartStaffPro portal to view further details.");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Failed to send leave status email: " + e.getMessage());
        }
    }

    // Fetch only the logged-in employee's leaves for their history table
    public List<LeaveRequest> getLeavesForEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    // Calculate remaining Annual and Casual leave balances
    public Map<String, Integer> calculateLeaveBalances(Long employeeId) {
        List<LeaveRequest> requests = getLeavesForEmployee(employeeId);
        int annualTaken = 0;
        int casualTaken = 0;

        for (LeaveRequest req : requests) {
            // We count both PENDING and APPROVED so they don't accidentally over-request!
            if (!"REJECTED".equals(req.getStatus())) {
                long days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;

                if ("ANNUAL".equals(req.getLeaveType())) annualTaken += days;
                if ("CASUAL".equals(req.getLeaveType())) casualTaken += days;
            }
        }

        // Standard limits: 14 Annual, 7 Casual
        Map<String, Integer> balances = new HashMap<>();
        balances.put("annual", Math.max(0, 14 - annualTaken));
        balances.put("casual", Math.max(0, 7 - casualTaken));

        return balances;
    }
}