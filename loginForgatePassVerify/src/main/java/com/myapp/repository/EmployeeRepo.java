package com.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myapp.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
    Employee findByVerificationCode(String code);
    Employee findByResetPasswordToken(String token);
}
