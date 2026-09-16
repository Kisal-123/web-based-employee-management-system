package com.emp.management.useraccess.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Routes
                        .requestMatchers("/register", "/forgot-password", "/reset-password-confirm", "/css/**").permitAll()

                        // 2. HR Manager Only
                        .requestMatchers("/employees/**").hasRole("HR_MANAGER")

                        // 3. Operations Manager Only
                        .requestMatchers("/leave/manage", "/expenses/manage").hasRole("OPERATIONS_MANAGER")

                        // 4. Shared Attendance Access
                        .requestMatchers("/attendance/**").hasAnyRole("OPERATIONS_MANAGER", "HR_MANAGER")

                        // 5. SHARED DOWNLOAD ROUTE (Must be ABOVE the next line!)
                        .requestMatchers("/payroll/download/**").hasAnyRole("FINANCE_EXECUTIVE", "EMPLOYEE")

                        // 6. Finance Executive Only
                        .requestMatchers("/payroll/**").hasRole("FINANCE_EXECUTIVE")

                        // 7. IT Officer Only
                        .requestMatchers("/audit-logs", "/admin/**").hasRole("IT_OFFICER")

                        // 8. Standard Employee Only
                        .requestMatchers("/leave/apply", "/expenses/apply", "/my-profile", "/my-payslips").hasRole("EMPLOYEE")

                        // 9. Catch-all
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                );
        return http.build();
    }
}


