package com.example.AuthService.repository;

import com.example.AuthService.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository
        extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmail(String email);
    Optional<EmailOtp> findByEmailAndOtp(
            String email,
            String otp
    );

    void deleteByEmail(String email);
}
