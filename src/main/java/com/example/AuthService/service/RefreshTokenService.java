package com.example.AuthService.service;

import com.example.AuthService.model.AppUser;
import com.example.AuthService.model.RefreshToken;
import com.example.AuthService.repository.RefreshTokenRepository;
import com.example.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshDurationMs;

    public RefreshToken createRefreshToken(Long userId) {

        AppUser user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));

        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {

            RefreshToken token = existingToken.get();

            token.setToken(UUID.randomUUID().toString());

            token.setExpiryDate(
                    Instant.now()
                            .plusMillis(refreshDurationMs));

            return refreshTokenRepository.save(token);
        }

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(
                                Instant.now()
                                        .plusMillis(refreshDurationMs))
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(
            RefreshToken token) {

        if (token.getExpiryDate()
                .isBefore(Instant.now())) {

            refreshTokenRepository.delete(token);

            throw new RuntimeException(
                    "Refresh Token Expired");
        }

        return token;
    }
}