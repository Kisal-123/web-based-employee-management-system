package com.emp.management.leaveattendance.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.service.EmployeeService;
import com.emp.management.leaveattendance.model.LeaveRequest;
import com.emp.management.leaveattendance.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.emp.management.useraccess.repository.UserRepository;
import com.emp.management.employeerecords.repository.EmployeeRepository;
import com.emp.management.useraccess.model.User;
import java.security.Principal;

@Controller
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private com.emp.management.notification.service.EmailService emailService;

    // 1. THE EMPLOYEE VIEW (Apply for leave & see balances)
    @GetMapping("/leave/apply")
    public String showEmployeeLeavePage(Model model, Principal principal) {
        // Find the currently logged-in user
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // Find their matching HR record using their email
        Employee employee = employeeRepository.findByEmail(user.getEmail());

        LeaveRequest request = new LeaveRequest();

        if (employee != null) {
            // Lock the new request to this specific employee
            request.setEmployee(employee);

            // Pass their real-time balances to the screen
            model.addAttribute("balances", leaveService.calculateLeaveBalances(employee.getId()));

            // Pass ONLY their specific leave history
            model.addAttribute("myLeaves", leaveService.getLeavesForEmployee(employee.getId()));
        }

        model.addAttribute("leaveRequest", request);
        model.addAttribute("employee", employee);

        return "leave-employee";
    }

    // 2. THE MANAGER VIEW (Approve/Reject dashboard)
    @GetMapping("/leave/manage")
    public String showManagerLeaveDashboard(Model model) {
        model.addAttribute("allLeaves", leaveService.getAllLeaveRequests());
        return "leave-manager";
    }

    // 3. Submits request, sends email, and redirects employee back to application page
    @PostMapping("/leave/submit")
    public String submitLeaveRequest(@ModelAttribute("leaveRequest") LeaveRequest leaveRequest) {
        // Save to the database first
        leaveService.submitLeaveRequest(leaveRequest);

        // Fetch the employee to get their actual name for the email
        Employee employee = employeeRepository.findById(leaveRequest.getEmployee().getId()).orElse(null);

        if (employee != null) {
            // Trigger the email to the Operations Manager
            // (You can change this placeholder email to your actual testing email address)
            emailService.sendLeaveNotification(
                    "kisalranaabey03@gmail.com",
                    employee.getFirstName(),
                    leaveRequest.getLeaveType()
            );
        }

        return "redirect:/leave/apply?success";
    }

    // 4. Approves/Rejects request and sends the manager back to their dashboard
    @PostMapping("/leave/update/{id}")
    public String updateLeaveStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        leaveService.updateLeaveStatus(id, status);
        return "redirect:/leave/manage";
    }

    // 5. THE OPERATIONS MANAGER VIEW (Real-Time Balances Directory)
    @GetMapping("/leave/balances")
    public String showAllLeaveBalances(Model model) {
        // Fetch all employees
        java.util.List<Employee> allEmployees = employeeRepository.findAll();

        // Create a list to hold both the employee info and their calculated balances
        java.util.List<java.util.Map<String, Object>> directoryData = new java.util.ArrayList<>();

        for (Employee emp : allEmployees) {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("employee", emp);

            // Re-use your existing engine to calculate this specific employee's balances
            data.put("balances", leaveService.calculateLeaveBalances(emp.getId()));

            directoryData.add(data);
        }

        model.addAttribute("directory", directoryData);
        return "leave-balances";
    }
}