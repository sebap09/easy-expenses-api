package com.easyexpenses.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "token")
public record TokenProperties(
        String secret,
        String issuer,
        String audience,
        Duration accessTokenExpiration,
        Duration refreshTokenExpiration
) {
}