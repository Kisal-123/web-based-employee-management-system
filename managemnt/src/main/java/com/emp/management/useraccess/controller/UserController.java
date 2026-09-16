package com.emp.management.useraccess.controller;

import com.emp.management.useraccess.model.User;
import com.emp.management.useraccess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // This returns your login.html file
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "reset-password";
    }

    // Add this anywhere inside your UserController class
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // This tells it to load forgot-password.html
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("newPassword") String newPassword, Authentication authentication) {
        // Get the currently logged-in user's username
        String username = authentication.getName();

        // Find them in the database
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use the resetPassword method you already built earlier!
        userService.resetPassword(user.getId(), newPassword);

        // Redirect them to the dashboard now that their password is updated
        return "redirect:/";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username) {
        try {
            userService.generateTemporaryPassword(username);
            System.out.println("SUCCESS: Email sent to user " + username);
        } catch (Exception e) {
            // THIS WILL PRINT THE EXACT ERROR TO YOUR INTELLIJ CONSOLE
            System.out.println("ERROR SENDING EMAIL: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/reset-password-confirm?username=" + username;
    }

    // 1. This handles showing the page when you get redirected
    @GetMapping("/reset-password-confirm")
    public String showResetConfirmForm(@RequestParam(value = "username", required = false) String username, Model model) {
        model.addAttribute("username", username);
        return "reset-password-confirm"; // Tells Spring to load reset-password-confirm.html
    }

    // 2. This handles what happens when you type in the new passwords and hit save
    @PostMapping("/reset-password-confirm")
    public String processResetConfirm(@RequestParam("username") String username,
                                      @RequestParam("tempPassword") String tempPassword,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match!");
            model.addAttribute("username", username);
            return "reset-password-confirm";
        }

        boolean success = userService.verifyAndSetNewPassword(username, tempPassword, newPassword);

        if (success) {
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", "Invalid temporary password.");
            model.addAttribute("username", username);
            return "reset-password-confirm";
        }
    }
}