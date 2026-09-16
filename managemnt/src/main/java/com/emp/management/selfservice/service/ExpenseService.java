package com.emp.management.selfservice.service;

import com.emp.management.selfservice.model.ExpenseClaim;
import com.emp.management.selfservice.repository.ExpenseClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseClaimRepository expenseRepository;

    // 1. Employee submits a claim
    public ExpenseClaim submitClaim(ExpenseClaim claim) {
        claim.setDateSubmitted(LocalDate.now());
        claim.setStatus("PENDING");
        return expenseRepository.save(claim);
    }

    // 2. Manager views all claims
    public List<ExpenseClaim> getAllClaims() {
        return expenseRepository.findAll();
    }

    // 3. Manager approves/rejects
    public void updateStatus(Long id, String status) {
        ExpenseClaim claim = expenseRepository.findById(id).orElseThrow();
        claim.setStatus(status);
        expenseRepository.save(claim);
    }

    // 4. THE MAGIC AUTOMATION FOR PAYROLL
    public double processApprovedExpensesForPayroll(Long employeeId) {
        // Fetch only claims that the manager has APPROVED
        List<ExpenseClaim> approvedClaims = expenseRepository.findByEmployeeIdAndStatus(employeeId, "APPROVED");

        double totalExpenseReimbursement = 0.0;

        for (ExpenseClaim claim : approvedClaims) {
            totalExpenseReimbursement += claim.getAmount();
            // Instantly mark as PAID so it isn't pulled again next month!
            claim.setStatus("PAID");
            expenseRepository.save(claim);
        }

        return totalExpenseReimbursement;
    }
}