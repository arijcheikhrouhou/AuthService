package com.cofat.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "blacklist:";

    // Ajoute un token à la liste noire, avec une durée de vie = temps restant avant expiration
    public void blacklistToken(String token, long remainingValidityMs) {
        if (remainingValidityMs > 0) {
            redisTemplate.opsForValue().set(
                    PREFIX + token,
                    "revoked",
                    remainingValidityMs,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    // Vérifie si un token est sur liste noire
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}