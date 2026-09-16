package com.emp.management.selfservice.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.repository.EmployeeRepository;
import com.emp.management.useraccess.model.User;
import com.emp.management.useraccess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.emp.management.payroll.service.PayrollService;

import java.security.Principal;

@Controller
public class SelfServiceController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayrollService payrollService;

    @GetMapping("/my-profile")
    public String viewMyProfile(Model model, Principal principal) {
        // 1. Find the logged-in user's account
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // 2. Find their matching HR record by email
        Employee employee = employeeRepository.findByEmail(user.getEmail());

        model.addAttribute("employee", employee);
        return "my-profile";
    }

    @PostMapping("/my-profile/update")
    public String updateMyProfile(@ModelAttribute("employee") Employee updatedEmployee) {
        // Fetch the existing record to ensure they don't overwrite secure HR data (like salary or status!)
        Employee existingEmployee = employeeRepository.findById(updatedEmployee.getId()).orElseThrow();

        // Update ONLY the allowed personal fields
        existingEmployee.setPhone(updatedEmployee.getPhone());
        existingEmployee.setEmergencyContactName(updatedEmployee.getEmergencyContactName());
        existingEmployee.setEmergencyContactPhone(updatedEmployee.getEmergencyContactPhone());

        employeeRepository.save(existingEmployee);

        return "redirect:/my-profile?success";
    }

    @GetMapping("/my-payslips")
    public String viewMyPayslips(Model model, Principal principal) {
        // Find who is logged in
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Employee employee = employeeRepository.findByEmail(user.getEmail());

        if (employee != null) {
            // Fetch ONLY their specific payslips
            model.addAttribute("payslips", payrollService.getPayslipsForEmployee(employee.getId()));
        }

        return "my-payslips";
    }
}