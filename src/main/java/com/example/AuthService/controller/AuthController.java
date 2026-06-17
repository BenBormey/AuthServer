package com.example.AuthService.controller;

import com.example.AuthService.dto.*;
import com.example.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody ResetPasswordRequest request) {

        return authService.resetPassword(
                request.getUsername(),
                request.getPassword());
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        return authService.forgotPassword(request);
    }
    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return authService.refreshToken(request);
    }
    @GetMapping("/verify-email")
    public String verifyEmail(
            @RequestParam String token) {

        return authService.verifyEmail(token);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        String result = authService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(result);
    }
}