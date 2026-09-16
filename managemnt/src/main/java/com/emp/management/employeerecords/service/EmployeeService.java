package com.emp.management.employeerecords.service;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Fetch all employees from the database
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Save a new employee
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    // Fetch a single employee by their ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + id));
    }

    // Offboard an employee by changing their status to TERMINATED
    public void offboardEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setStatus("TERMINATED");
        employeeRepository.save(employee);
    }
}
