package com.easyexpenses.api.services;

import com.easyexpenses.api.config.TokenProperties;
import com.easyexpenses.api.security.JwtGenerationRequest;
import com.easyexpenses.api.security.JwtService;
import org.junit.jupiter.api.Test;


import java.time.Clock;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtServiceTest {

    @Test
    void shouldRejectExpiredToken(){
        TokenProperties tokenProperties = tokenProperties();
        Clock now = Clock.systemUTC();
        JwtService jwtService = new JwtService(tokenProperties, now);

        String token = jwtService.generateToken(new JwtGenerationRequest("123")).token();

        Clock futureClock = Clock.offset(now, Duration.ofDays(10));
        JwtService serviceLater = new JwtService(tokenProperties, futureClock);

        assertTrue(serviceLater.isExpired(token));
    }

    @Test
    void shouldAllowNotExpiredToken(){
        TokenProperties tokenProperties = tokenProperties();

        JwtService jwtService = new JwtService(tokenProperties, Clock.systemUTC());
        String token = jwtService.generateToken(new JwtGenerationRequest("123")).token();

        assertFalse(jwtService.isExpired(token));
    }

    private TokenProperties tokenProperties() {
        return new TokenProperties(
                "secretsecretsecretsecretsecret123",
                "issuer",
                "audience",
                Duration.ofMinutes(10),
                Duration.ofMinutes(10)
        );
    }
}
