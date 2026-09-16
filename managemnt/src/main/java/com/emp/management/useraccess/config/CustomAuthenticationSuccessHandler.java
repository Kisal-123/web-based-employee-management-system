package com.emp.management.useraccess.config;

import com.emp.management.useraccess.model.User;
import com.emp.management.useraccess.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Get the username of the person who just logged in
        String username = authentication.getName();

        // Look them up in the database
        User user = userRepository.findByUsername(username).orElse(null);

        // Redirect based on the firstLogin flag
        if (user != null && user.isFirstLogin()) {
            response.sendRedirect("/reset-password");
        } else {
            response.sendRedirect("/");
        }
    }
}