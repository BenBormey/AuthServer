package com.example.AuthService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"Users\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")
    private Long id;

    @Column(name = "\"Username\"", nullable = false, unique = true)
    private String username;

    @Column(name = "\"PasswordHash\"", nullable = false)
    private String passwordHash;

    @Column(name = "\"Email\"", nullable = false, unique = true)
    private String email;

    @Column(name = "\"Role\"", nullable = false)
    private String role;
}