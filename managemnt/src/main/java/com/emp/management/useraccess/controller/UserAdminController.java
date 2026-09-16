package com.emp.management.useraccess.controller;

import com.emp.management.useraccess.service.AuditLogService;
import com.emp.management.useraccess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class UserAdminController {

    @Autowired
    private UserService userService;

    // We inject the tracker so we can record role changes!
    @Autowired
    private AuditLogService auditLogService;

    // Load the dashboard
    @GetMapping("/admin/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user-admin";
    }

    // Process the role change
    @PostMapping("/admin/users/update-role/{id}")
    public String updateUserRole(@PathVariable("id") Long id,
                                 @RequestParam("role") String role,
                                 Principal principal) {

        userService.updateUserRole(id, role);

        // --- BLACKBOX TRACKER ---
        auditLogService.logAction(principal.getName(), "UPDATE_ROLE", "Changed role for User ID " + id + " to " + role);

        return "redirect:/admin/users?success";
    }
}