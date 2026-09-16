package com.emp.management.payroll.repository;

import com.emp.management.payroll.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    // Allows the Finance Executive to pull all records for a specific month
    List<Payroll> findByPayPeriod(String payPeriod);

    // Allows the Employee to view their personal past payslips
    List<Payroll> findByEmployeeId(Long employeeId);
}