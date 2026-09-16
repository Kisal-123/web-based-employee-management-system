package com.emp.management.useraccess.repository;

import com.emp.management.useraccess.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Allows the IT Officer to find logs by a specific user
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);

    // Allows the IT Officer to see the most recent logs first
    List<AuditLog> findAllByOrderByTimestampDesc();
}