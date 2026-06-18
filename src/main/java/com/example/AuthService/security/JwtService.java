package com.example.AuthService.security;

import com.example.AuthService.model.AppUser;
import com.example.AuthService.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    "mysecretkeymysecretkeymysecretkey123456"
                            .getBytes()
            );

    public String generateToken(AppUser user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put(
                "roles",
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .toList()
        );

        return createToken(
                claims,
                user.getUsername()
        );
    }

    private String createToken(
            Map<String, Object> claims,
            String username
    ) {

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 60 * 24
                        )
                )
                .signWith(key)
                .compact();
    }
}