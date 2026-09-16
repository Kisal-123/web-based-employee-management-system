package com.emp.management.payroll.service;

import com.emp.management.payroll.model.Payroll;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PayrollPdfService {

    public byte[] generatePayslipPdf(Payroll payroll) {
        // This holds the PDF data in memory before we send it to the browser
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create a standard A4 document
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Setup our fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        // Draw the Document Header
        document.add(new Paragraph("SmartStaffPro - Official Payslip", titleFont));
        document.add(new Paragraph("Pay Period: " + payroll.getPayPeriod(), subTitleFont));
        document.add(new Paragraph(" ")); // Blank space

        // Draw Employee Details
        document.add(new Paragraph("Employee Information", subTitleFont));
        document.add(new Paragraph("Name: " + payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName(), normalFont));
        document.add(new Paragraph("Department: " + payroll.getEmployee().getDepartment(), normalFont));
        document.add(new Paragraph("Designation: " + payroll.getEmployee().getDesignation(), normalFont));
        document.add(new Paragraph("---------------------------------------------------"));

        // Draw Earnings
        document.add(new Paragraph("Earnings", subTitleFont));
        document.add(new Paragraph("Basic Salary: Rs. " + payroll.getBasicSalary(), normalFont));
        document.add(new Paragraph("Overtime Pay: Rs. " + payroll.getOvertimePay(), normalFont));
        document.add(new Paragraph("Allowances: Rs. " + payroll.getAllowances(), normalFont));
        document.add(new Paragraph("Gross Salary: Rs. " + payroll.getGrossSalary(), subTitleFont));
        document.add(new Paragraph("---------------------------------------------------"));

        // Draw Deductions
        document.add(new Paragraph("Deductions", subTitleFont));
        document.add(new Paragraph("EPF Deduction (8%): Rs. " + payroll.getEpfDeduction(), normalFont));
        document.add(new Paragraph("APIT Tax: Rs. " + payroll.getApitTax(), normalFont));
        document.add(new Paragraph("Other Deductions/Penalties: Rs. " + payroll.getDeductions(), normalFont));
        document.add(new Paragraph("---------------------------------------------------"));

        // Draw Final Net Pay
        document.add(new Paragraph("NET TAKE-HOME PAY: Rs. " + payroll.getNetSalary(), titleFont));

        document.close();

        // Return the finished PDF file as a byte array
        return baos.toByteArray();
    }
}