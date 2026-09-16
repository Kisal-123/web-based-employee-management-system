package com.emp.management.employeerecords.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.emp.management.useraccess.service.AuditLogService;
import java.security.Principal;
import com.emp.management.reportmanagement.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/employees")
    public String viewEmployeeList(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employee-list";
    }

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-form";
    }

    // --- UPDATED: Catch the MultipartFile for Document Storage ---
    @PostMapping("/employees")
    public String saveEmployee(@ModelAttribute("employee") Employee employee,
                               @RequestParam(value = "contractFile", required = false) MultipartFile contractFile,
                               Principal principal) {

        // Handle the file upload securely
        try {
            if (contractFile != null && !contractFile.isEmpty()) {
                String uploadDir = "C:/smartstaffpro_uploads/contracts/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs(); // Create the folder if it doesn't exist
                }

                String filePath = uploadDir + employee.getFirstName() + "_" + contractFile.getOriginalFilename();
                contractFile.transferTo(new File(filePath));

                // Save the file path to the database
                employee.setContractPath(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to upload the contract file.");
        }

        employeeService.saveEmployee(employee);

        // --- BLACKBOX TRACKER ---
        auditLogService.logAction(principal.getName(), "CREATE_OR_UPDATE_EMPLOYEE", "Saved record for: " + employee.getFirstName() + " " + employee.getLastName());

        return "redirect:/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "employee-form";
    }

    @GetMapping("/employees/view/{id}")
    public String viewEmployeeDetails(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "employee-view";
    }

    @GetMapping("/employees/export")
    public ResponseEntity<byte[]> exportEmployeeReport() {
        byte[] csvData = reportService.generateEmployeeSummaryCsv(employeeService.getAllEmployees());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=HR_Employee_Summary.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return ResponseEntity.ok().headers(headers).body(csvData);
    }

    @PostMapping("/employees/offboard/{id}")
    public String offboardEmployee(@PathVariable("id") Long id, Principal principal) {
        Employee employee = employeeService.getEmployeeById(id);
        employeeService.offboardEmployee(id);

        // Log the offboarding action
        auditLogService.logAction(principal.getName(), "OFFBOARD_EMPLOYEE", "Offboarded employee ID: " + id);

        return "redirect:/employees";
    }
}