package com.emp.management.employeerecords.repository;

import com.emp.management.employeerecords.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Spring Boot automatically writes the SQL queries for saving, finding, and deleting!
    Employee findByEmail(String email);
}