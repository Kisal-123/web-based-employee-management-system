package com.emp.management.payroll.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.service.EmployeeService;
import com.emp.management.payroll.model.Payroll;
import com.emp.management.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.emp.management.payroll.service.PayrollPdfService;
import com.emp.management.useraccess.service.AuditLogService;
import java.security.Principal;
import java.io.File;
import java.nio.file.Files;

@Controller
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private com.emp.management.notification.service.EmailService emailService;

    @Autowired
    private PayrollPdfService payrollPdfService;

    @GetMapping("/payroll")
    public String showPayrollDashboard(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("payslips", payrollService.getAllPayslips());
        return "payroll-dashboard";
    }

    @PostMapping("/payroll/generate")
    public String generatePayroll(@RequestParam("employeeId") Long employeeId,
                                  @RequestParam("payPeriod") String payPeriod,
                                  @RequestParam(value = "allowances", defaultValue = "0.0") Double allowances,
                                  @RequestParam(value = "deductions", defaultValue = "0.0") Double deductions,
                                  Principal principal) {

        Employee employee = employeeService.getEmployeeById(employeeId);

        // 1. Generate and save the payroll (Capture the saved object)
        Payroll generatedPayroll = payrollService.generatePayslip(employee, payPeriod, allowances, deductions);

        // 2. --- BLACKBOX TRACKER ---
        auditLogService.logAction(principal.getName(), "GENERATE_PAYROLL", "Generated payslip for " + employee.getFirstName() + " for period: " + payPeriod);

        // 3. --- AUTOMATED PDF EMAIL ATTACHMENT ---
        try {
            // Generate the PDF in memory
            byte[] pdfBytes = payrollPdfService.generatePayslipPdf(generatedPayroll);

            // Create a temporary file on the server to hold the PDF
            File tempPdf = File.createTempFile("Payslip_" + employee.getFirstName() + "_" + payPeriod, ".pdf");
            Files.write(tempPdf.toPath(), pdfBytes);

            // Send the email (Make sure the employee's email in your DB is set to your test email!)
            emailService.sendPayslipEmail(
                    employee.getEmail(),
                    employee.getFirstName(),
                    generatedPayroll.getPayPeriod(),
                    tempPdf.getAbsolutePath()
            );

            // Tell the server to delete the temporary file after it's done to save space
            tempPdf.deleteOnExit();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not generate or attach PDF for email.");
        }

        return "redirect:/payroll?success";
    }

    // 1. Delete a payslip
    @PostMapping("/payroll/delete/{id}")
    public String deletePayroll(@PathVariable("id") Long id) {
        payrollService.deletePayslip(id);
        return "redirect:/payroll?deleted";
    }

    // 2. Show the Edit Form
    @GetMapping("/payroll/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("payroll", payrollService.getPayslipById(id));
        return "payroll-edit";
    }

    // 3. Process the Update
    @PostMapping("/payroll/update/{id}")
    public String updatePayroll(@PathVariable("id") Long id,
                                @RequestParam("allowances") Double allowances,
                                @RequestParam("deductions") Double deductions) {
        payrollService.updatePayslip(id, allowances, deductions);
        return "redirect:/payroll?updated";
    }

    // --- DOWNLOAD ROUTE ---
    @GetMapping("/payroll/download/{id}")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable("id") Long id) {

        // Fetch the payslip from the database
        Payroll payroll = payrollService.getPayslipById(id);

        // Generate the PDF
        byte[] pdfBytes = payrollPdfService.generatePayslipPdf(payroll);

        // Tell the browser this is a PDF file that should be downloaded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // This sets the default filename when they save it to their computer
        headers.setContentDispositionFormData("attachment", "Payslip_" + payroll.getEmployee().getFirstName() + "_" + payroll.getPayPeriod() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}