package com.emp.management.performance.controller;

import com.emp.management.employeerecords.model.Employee;
import com.emp.management.employeerecords.repository.EmployeeRepository;
import com.emp.management.performance.model.PerformanceReview;
import com.emp.management.performance.repository.PerformanceReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private PerformanceReviewRepository performanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // 1. View all performance reviews
    @GetMapping
    public String viewPerformanceHub(Model model) {
        List<PerformanceReview> reviews = performanceRepository.findAll();
        model.addAttribute("reviews", reviews);
        return "performance-list"; // We will create this HTML file next
    }

    // 2. Show the Evaluation Form
    @GetMapping("/evaluate")
    public String showEvaluationForm(Model model) {
        // We need the list of employees to populate the dropdown menu
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("review", new PerformanceReview());
        return "performance-form";
    }

    // 3. Save the Evaluation & Calculate Bonus
    @PostMapping("/save")
    public String saveEvaluation(@ModelAttribute("review") PerformanceReview review) {

        // --- AUTOMATED BONUS CALCULATION ENGINE ---
        double bonus = 0.0;
        if (review.getRating() != null) {
            switch (review.getRating()) {
                case 5: bonus = 15.0; break; // 15% Bonus for Outstanding
                case 4: bonus = 10.0; break; // 10% Bonus for Exceeds Expectations
                case 3: bonus = 5.0;  break; // 5% Bonus for Meets Expectations
                case 2: bonus = 0.0;  break; // 0% Bonus for Needs Improvement
                case 1: bonus = 0.0;  break; // 0% Bonus for Unsatisfactory
                default: bonus = 0.0; break;
            }
        }
        review.setBonusPercentage(bonus);

        // Save to the database
        performanceRepository.save(review);

        return "redirect:/performance"; // Redirect back to the hub
    }
}