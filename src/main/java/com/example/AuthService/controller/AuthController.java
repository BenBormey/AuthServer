package com.example.AuthService.controller;

import com.example.AuthService.dto.*;
import com.example.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private  final AuthService authService;


    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request
            ){
        return   authService.register((request));

    }
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request) {

        return authService.Login(request);
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam  String username,
                                @RequestParam String newPassword) {

        return authService.resetPassword(username, newPassword);
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        return authService.forgotPassword(request);
    }





}
