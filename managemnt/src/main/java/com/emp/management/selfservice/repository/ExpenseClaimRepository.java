package com.emp.management.selfservice.repository;

import com.emp.management.selfservice.model.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Long> {
    // For the Employee Dashboard
    List<ExpenseClaim> findByEmployeeId(Long employeeId);

    // For the Automated Payroll Engine!
    List<ExpenseClaim> findByEmployeeIdAndStatus(Long employeeId, String status);
}