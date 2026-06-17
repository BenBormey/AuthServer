package com.example.AuthService.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String username;
    private String password;
}