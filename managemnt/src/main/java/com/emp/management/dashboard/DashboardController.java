package com.emp.management.dashboard;

import com.emp.management.leaveattendance.model.LeaveRequest;
import com.emp.management.leaveattendance.repository.LeaveRequestRepository;
import com.emp.management.payroll.model.Payroll;
import com.emp.management.payroll.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private LeaveRequestRepository leaveRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("role", role);

            // --- 1. LEAVE PIE CHART DATA (For Operations/HR) ---
            List<LeaveRequest> allLeaves = leaveRepository.findAll();
            long annualCount = 0;
            long casualCount = 0;
            long medicalCount = 0;
            long unpaidCount = 0;

            for (LeaveRequest leave : allLeaves) {
                if ("APPROVED".equals(leave.getStatus())) {
                    if ("ANNUAL".equals(leave.getLeaveType())) annualCount++;
                    if ("CASUAL".equals(leave.getLeaveType())) casualCount++;
                    if ("MEDICAL".equals(leave.getLeaveType())) medicalCount++;
                    // Note: Adjust "UNPAID" if your database saves it as "NON_PAY" or "NOPAY"
                    if ("UNPAID".equals(leave.getLeaveType()) || "NON_PAY".equals(leave.getLeaveType())) unpaidCount++;
                }
            }

            model.addAttribute("approvedAnnualCount", annualCount);
            model.addAttribute("approvedCasualCount", casualCount);
            model.addAttribute("approvedMedicalCount", medicalCount);
            model.addAttribute("approvedUnpaidCount", unpaidCount);

            // --- 2. PAYROLL BAR CHART DATA (For Finance) ---
            List<Payroll> allPayrolls = payrollRepository.findAll();
            // Using LinkedHashMap to keep the months in order
            Map<String, Double> payrollByMonth = new LinkedHashMap<>();

            for (Payroll p : allPayrolls) {
                // Assuming getPayPeriod() returns something like "Sept 2026"
                String month = p.getPayPeriod();
                double currentTotal = payrollByMonth.getOrDefault(month, 0.0);
                payrollByMonth.put(month, currentTotal + p.getNetSalary()); // Summing up Net Salary
            }

            // Pass the X-axis (Months) and Y-axis (Totals) to the frontend
            model.addAttribute("payrollMonths", payrollByMonth.keySet());
            model.addAttribute("payrollTotals", payrollByMonth.values());
        }

        return "dashboard";
    }
}