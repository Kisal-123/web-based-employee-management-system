package com.emp.management.selfservice.controller;

import com.emp.management.employeerecords.service.EmployeeService;
import com.emp.management.selfservice.model.ExpenseClaim;
import com.emp.management.selfservice.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private EmployeeService employeeService;

    // 1. THE EMPLOYEE VIEW (Submit a claim)
    @GetMapping("/expenses/apply")
    public String showEmployeeExpensePage(Model model) {
        model.addAttribute("expenseClaim", new ExpenseClaim());
        model.addAttribute("employees", employeeService.getAllEmployees());

        // Pass the history to the employee dashboard
        model.addAttribute("myExpenses", expenseService.getAllClaims());
        return "expense-employee";
    }

    // 2. Process a new claim submission
    @PostMapping("/expenses/submit")
    public String submitExpenseClaim(@ModelAttribute("expenseClaim") ExpenseClaim expenseClaim) {
        expenseService.submitClaim(expenseClaim);
        return "redirect:/expenses/apply?success";
    }

    // 3. THE MANAGER VIEW (Approve/Reject dashboard)
    @GetMapping("/expenses/manage")
    public String showManagerExpenseDashboard(Model model) {
        model.addAttribute("allExpenses", expenseService.getAllClaims());
        return "expense-manager";
    }

    // 4. Process manager approvals or rejections
    @PostMapping("/expenses/update/{id}")
    public String updateExpenseStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        expenseService.updateStatus(id, status);
        return "redirect:/expenses/manage";
    }
}