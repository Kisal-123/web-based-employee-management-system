package com.emp.management.useraccess.controller;

import com.emp.management.useraccess.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    // Only the IT Officer will be able to access this route
    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getAllLogs());
        return "audit-logs";
    }
}