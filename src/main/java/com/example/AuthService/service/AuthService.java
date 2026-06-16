package com.example.AuthService.service;

import com.example.AuthService.AuthServiceApplication;
import com.example.AuthService.dto.*;
import com.example.AuthService.model.AppUser;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);

        return new AuthResponse("Register Success");
    }


    public AuthResponse Login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElse(null);


        if(user == null){
            return new AuthResponse("User Not food");
        }

        boolean valid  = passwordEncoder.matches((request.getPassword()),
                user.getPasswordHash()
                );

        if(!valid)
        {
            return   new AuthResponse("Invalid password");
        }
        String Token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(Token);
    }
    public String changePassword(ChangePasswordRequest request) {

        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPasswordHash())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Old password is incorrect");
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        return "Password changed successfully";
    }
    public String resetPassword(String username, String newPassword) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"));

        user.setPasswordHash(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return "Password reset successfully";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Email not found"));

        return "Reset link sent to email";
    }


}


