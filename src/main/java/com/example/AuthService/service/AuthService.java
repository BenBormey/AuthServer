package com.example.AuthService.service;
import com.example.AuthService.model.*;
import com.example.AuthService.repository.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.AuthService.dto.*;
import com.example.AuthService.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    public AuthService(
            RefreshTokenRepository refreshTokenRepository, EmailOtpRepository emailOtpRepository, JavaMailSender mailSender, UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService, RoleRepository roleRepository,
            RefreshTokenService refreshTokenService, VerificationTokenRepository verificationTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailOtpRepository = emailOtpRepository;
        this.mailSender = mailSender;

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse("Email already exists");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        EmailOtp emailOtp = EmailOtp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        emailOtpRepository.save(emailOtp);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText(
                "Hello " + user.getUsername()
                        + "\n\nYour OTP Code is : "
                        + otp
                        + "\n\nThis code expires in 5 minutes."
        );




        mailSender.send(message);

        return new AuthResponse("OTP sent to your email.");
    }
    public AuthResponse login(LoginRequest request) {

        AppUser user = userRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return new AuthResponse("User Not Found");
        }

        if (!user.isEnabled()) {
            return new AuthResponse(
                    "Please verify your email first");
        }

        boolean valid = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!valid) {
            return new AuthResponse(
                    "Invalid Password");
        }

        String accessToken =
                jwtService.generateToken(user);

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(
                                user.getId()
                        );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Login Success"
        );
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

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Email not found"));

        return "Reset link sent to email";
    }


    public AuthResponse refreshToken(
            RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"));

        refreshTokenService
                .verifyExpiration(refreshToken);

        String accessToken =
                jwtService.generateToken(
                        refreshToken.getUser()
                );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Token Refreshed"
        );
    }
    public String verifyEmail(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid token"));

        if (verificationToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Token expired");
        }

        AppUser user =
                verificationToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        verificationTokenRepository
                .delete(verificationToken);

        return "Email verified successfully";
    }
    public String verifyOtp(String email, String otp) {

        EmailOtp emailOtp =
                emailOtpRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("OTP not found"));

        if (!emailOtp.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        AppUser user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        user.setEnabled(true);

        userRepository.save(user);

        emailOtpRepository.delete(emailOtp);

        return "Email verified successfully";
    }
}