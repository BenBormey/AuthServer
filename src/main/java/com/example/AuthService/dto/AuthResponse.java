package com.example.AuthService.dto;

import lombok.*;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }
}