package com.easyexpenses.api.dtos;

public record AuthResponse(String accessToken, String tokenType, Long expiresIn, String refreshToken) {
}
