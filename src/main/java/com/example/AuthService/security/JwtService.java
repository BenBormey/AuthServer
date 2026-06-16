package com.example.AuthService.security;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    "mysecretkeymysecretkeymysecretkey123456"
                            .getBytes());

    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }
}
