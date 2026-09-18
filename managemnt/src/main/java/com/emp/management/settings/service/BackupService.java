package com.emp.management.settings.service; // Adjust package name if needed

import com.emp.management.useraccess.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    @Autowired
    private AuditLogService auditLogService;

    // Magically pulls your database credentials from application.properties
    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * For final submission: @Scheduled(cron = "0 59 23 * * ?") -> Runs at 11:59 PM every night.
     * For TESTING right now: @Scheduled(fixedRate = 60000) -> Runs every 60 seconds so you can see it work!
     */
    @Scheduled(fixedRate = 60000)
    public void performAutomatedBackup() {
        String backupFolder = "C:/smartstaffpro_uploads/backups/";
        File directory = new File(backupFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a unique filename based on the exact second the backup runs
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "backup_smartstaffpro_" + timestamp + ".sql";
        String filePath = backupFolder + fileName;

        // The Windows terminal command to dump the MySQL database
        // NOTE: Make sure "smartstaffpro" matches your exact database name!
        String mysqldumpPath = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe";

        String command = String.format("\"%s\" -u%s -p%s smartstaffpro -r \"%s\"", mysqldumpPath, dbUsername, dbPassword, filePath);

        try {
            // Build the command dynamically
            java.util.List<String> commandList = new java.util.ArrayList<>();
            commandList.add(mysqldumpPath); // The path you set in the previous step
            commandList.add("-u" + dbUsername);

            // Only add the password flag if your database actually uses one!
            if (dbPassword != null && !dbPassword.trim().isEmpty()) {
                commandList.add("-p" + dbPassword);
            }

            commandList.add("ems_db"); // Your database name
            commandList.add("-r");
            commandList.add(filePath);

            ProcessBuilder pb = new ProcessBuilder(commandList);
            // This catches the hidden error message from MySQL
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read the exact error message and print it to your IntelliJ console!
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("MYSQL ERROR DETAILS: " + line);
            }

            int processComplete = process.waitFor();

            if (processComplete == 0) {
                System.out.println("✅ Backup completed successfully: " + fileName);
                auditLogService.logAction("SYSTEM_SCHEDULER", "DATABASE_BACKUP", "Automated backup generated: " + fileName);
            } else {
                System.out.println("❌ Backup failed! Error code: " + processComplete);
                auditLogService.logAction("SYSTEM_SCHEDULER", "BACKUP_FAILED", "Automated backup failed to generate.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
