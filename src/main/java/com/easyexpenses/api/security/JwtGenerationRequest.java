package com.easyexpenses.api.security;

import java.time.Duration;

public record JwtGenerationRequest(String subject, Duration expiresIn, String issuer, String audience) {
}
