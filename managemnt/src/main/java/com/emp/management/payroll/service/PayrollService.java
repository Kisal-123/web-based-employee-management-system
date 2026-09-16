package com.emp.management.payroll.service;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.leaveattendance.model.Attendance;
import com.emp.management.leaveattendance.service.AttendanceService;
import com.emp.management.payroll.model.Payroll;
import com.emp.management.payroll.repository.PayrollRepository;
import com.emp.management.performance.model.PerformanceReview;
import com.emp.management.performance.repository.PerformanceReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollService {

    @Autowired
    private com.emp.management.leaveattendance.service.LeaveService leaveService;

    @Autowired
    private com.emp.management.selfservice.service.ExpenseService expenseService;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private PerformanceReviewRepository performanceRepository;

    @Autowired
    private com.emp.management.settings.repository.CompanySettingsRepository settingsRepository;

    public Payroll generatePayslip(Employee employee, String payPeriod, Double manualAllowances, Double manualDeductions) {
        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setPayPeriod(payPeriod);

        // 1. Fetch Basic Salary
        Double basic = employee.getBasicSalary();
        if (basic == null) basic = 0.0;
        payroll.setBasicSalary(basic);

        // 2. Fetch Automated OT
        List<Attendance> logs = attendanceService.getAttendanceForEmployee(employee.getId());
        double totalOvertimeHours = 0.0;
        for (Attendance log : logs) {
            if (log.getOvertimeHours() != null) {
                totalOvertimeHours += log.getOvertimeHours();
            }
        }
        double hourlyRate = basic / 240.0;
        double otPay = totalOvertimeHours * hourlyRate * 1.5;
        payroll.setOvertimePay(Math.round(otPay * 100.0) / 100.0);

        // 3. Automated Unpaid Leave Penalties
        int unpaidDays = leaveService.processUnpaidLeaveForPayroll(employee.getId());
        double unpaidPenalty = 0.0;
        if (unpaidDays > 0) {
            double dailyRate = basic / 30.0;
            unpaidPenalty = unpaidDays * dailyRate;
        }

        // 4. Fetch Approved Expenses
        double automatedExpenses = expenseService.processApprovedExpensesForPayroll(employee.getId());

        // 5. FETCH PERFORMANCE BONUS
        double performanceBonusAmount = 0.0;
        PerformanceReview latestReview = performanceRepository.findTopByEmployeeIdOrderByIdDesc(employee.getId());

        if (latestReview != null && latestReview.getBonusPercentage() != null && latestReview.getBonusPercentage() > 0) {
            performanceBonusAmount = (basic * latestReview.getBonusPercentage()) / 100.0;
        }

        // 6. Consolidate Allowances & Deductions
        if (manualAllowances == null) manualAllowances = 0.0;
        double totalAllowances = manualAllowances + automatedExpenses + performanceBonusAmount;
        payroll.setAllowances(Math.round(totalAllowances * 100.0) / 100.0);

        if (manualDeductions == null) manualDeductions = 0.0;
        double totalDeductions = manualDeductions + unpaidPenalty;
        payroll.setDeductions(Math.round(totalDeductions * 100.0) / 100.0);

        // 7. GROSS SALARY
        double gross = basic + otPay + totalAllowances;
        payroll.setGrossSalary(Math.round(gross * 100.0) / 100.0);

        // 8. DYNAMIC EPF & ETF (From Settings Database)
        com.emp.management.settings.model.CompanySettings config = settingsRepository.findById(1L)
                .orElse(new com.emp.management.settings.model.CompanySettings());

        double epfDeduction = basic * (config.getEpfEmployeePercentage() / 100.0);
        double employerEpf = basic * (config.getEpfEmployerPercentage() / 100.0);
        double employerEtf = basic * (config.getEtfEmployerPercentage() / 100.0);

        payroll.setEpfDeduction(Math.round(epfDeduction * 100.0) / 100.0);
        payroll.setEmployerEpf(Math.round(employerEpf * 100.0) / 100.0);
        payroll.setEmployerEtf(Math.round(employerEtf * 100.0) / 100.0);

        // 9. APIT TAX CALCULATION
        double assessableIncome = gross - epfDeduction;
        double apitTax = calculateAPIT(assessableIncome);
        payroll.setApitTax(apitTax);

        // 10. NET SALARY
        double net = gross - epfDeduction - apitTax - totalDeductions;
        payroll.setNetSalary(Math.round(net * 100.0) / 100.0);

        // Save and return the generated object (No email logic here!)
        return payrollRepository.save(payroll);
    }

    // --- SRI LANKA PROGRESSIVE TAX ALGORITHM ---
    private double calculateAPIT(double income) {
        double tax = 0.0;
        if (income <= 150000) return 0.0;
        else if (income <= 233333) tax = (income - 150000) * 0.06;
        else if (income <= 275000) tax = (83333 * 0.06) + ((income - 233333) * 0.18);
        else if (income <= 316667) tax = (83333 * 0.06) + (41667 * 0.18) + ((income - 275000) * 0.24);
        else if (income <= 358333) tax = (83333 * 0.06) + (41667 * 0.18) + (41667 * 0.24) + ((income - 316667) * 0.30);
        else tax = (83333 * 0.06) + (41667 * 0.18) + (41667 * 0.24) + (41667 * 0.30) + ((income - 358333) * 0.36);
        return Math.round(tax * 100.0) / 100.0;
    }

    public Payroll getPayslipById(Long id) {
        return payrollRepository.findById(id).orElseThrow();
    }

    public void deletePayslip(Long id) {
        payrollRepository.deleteById(id);
    }

    public void updatePayslip(Long id, Double manualAllowances, Double manualDeductions) {
        Payroll payroll = getPayslipById(id);
        if (manualAllowances == null) manualAllowances = 0.0;
        if (manualDeductions == null) manualDeductions = 0.0;
        payroll.setAllowances(manualAllowances);
        payroll.setDeductions(manualDeductions);

        double gross = payroll.getBasicSalary() + payroll.getOvertimePay() + manualAllowances;
        payroll.setGrossSalary(Math.round(gross * 100.0) / 100.0);

        double assessableIncome = gross - payroll.getEpfDeduction();
        double apitTax = calculateAPIT(assessableIncome);
        payroll.setApitTax(apitTax);

        double net = gross - payroll.getEpfDeduction() - apitTax - manualDeductions;
        payroll.setNetSalary(Math.round(net * 100.0) / 100.0);

        payrollRepository.save(payroll);
    }

    public List<Payroll> getAllPayslips() {
        return payrollRepository.findAll();
    }

    public List<Payroll> getPayslipsForEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }
}