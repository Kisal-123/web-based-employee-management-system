package com.emp.management.useraccess.service;

import com.emp.management.useraccess.model.Role;
import com.emp.management.useraccess.model.User;
import com.emp.management.useraccess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // 1. Method for Registration
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 2. Method to find user (used by Controller)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 3. Method for First-Time Login Password Reset
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userRepository.save(user);
    }

    // 4. Method for Forgot Password (generates random string and triggers email)
    public String generateTemporaryPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setFirstLogin(true);
        userRepository.save(user);

        // Send the real email to the user!
        sendRealEmail(user.getEmail(), tempPassword);

        return tempPassword;
    }

    // 5. Method to actually build and send the email
    private void sendRealEmail(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kisalranaabey03@gmail.com");
        message.setTo(toEmail);
        message.setSubject("SmartStaffPro - Password Reset");
        message.setText("Hello,\n\nYour temporary password is: " + tempPassword +
                "\n\nPlease log in to set a new permanent password.");

        mailSender.send(message);
    }

    // 6. Method to verify temp password and set a new one
    public boolean verifyAndSetNewPassword(String username, String tempPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        // Check if the temporary password matches
        if (passwordEncoder.matches(tempPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setFirstLogin(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    // Fetch all registered user accounts for the IT Officer
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update a user's role
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        user.setRole(Role.valueOf(newRole));
        userRepository.save(user);
    }
}