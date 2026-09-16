package com.emp.management.settings.controller;

import com.emp.management.settings.model.CompanySettings;
import com.emp.management.settings.repository.CompanySettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('HR_MANAGER')") // Security: ONLY HR Managers can change global rules
public class SettingsController {

    @Autowired
    private CompanySettingsRepository settingsRepository;

    @GetMapping
    public String viewSettings(Model model) {
        // Fetch settings row #1. If it doesn't exist, create a new one with default values.
        CompanySettings settings = settingsRepository.findById(1L).orElse(new CompanySettings());
        settingsRepository.save(settings); // Ensure it's saved in DB

        model.addAttribute("settings", settings);
        return "settings-form";
    }

    @PostMapping("/update")
    public String updateSettings(@ModelAttribute("settings") CompanySettings updatedSettings) {
        // Force the ID to 1 so we overwrite the existing row, not create a new one!
        updatedSettings.setId(1L);
        settingsRepository.save(updatedSettings);

        return "redirect:/settings?success";
    }
}