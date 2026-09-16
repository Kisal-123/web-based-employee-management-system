package com.emp.management.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 1. Simple text email for the Operations Manager (Leave Notifications)
    public void sendLeaveNotification(String managerEmail, String employeeName, String leaveType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your.email@gmail.com");
        message.setTo(managerEmail);
        message.setSubject("New Leave Request: " + employeeName);
        message.setText("Hello Operations Manager,\n\n" +
                "A new " + leaveType + " leave request has been submitted by " + employeeName + ".\n" +
                "Please log in to SmartStaffPro to review and approve this request.\n\n" +
                "Thank you,\nSmartStaffPro System");

        mailSender.send(message);
    }

    // 2. Complex email with PDF Attachment for the Employee (Payroll)
    public void sendPayslipEmail(String employeeEmail, String employeeName, String month, String pdfFilePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // 'true' indicates multipart (attachment)

            helper.setFrom("your.email@gmail.com");
            helper.setTo(employeeEmail);
            helper.setSubject("Your Payslip for " + month);
            helper.setText("Hello " + employeeName + ",\n\n" +
                    "Congratulations on another great month! Your payroll for " + month + " has been processed.\n" +
                    "Please find your official PDF payslip attached to this email.\n\n" +
                    "Best regards,\nSmartStaffPro Finance Team");

            // Attach the PDF file
            FileSystemResource file = new FileSystemResource(new File(pdfFilePath));
            helper.addAttachment("Payslip_" + month + ".pdf", file);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send payslip email with attachment.");
        }
    }
}