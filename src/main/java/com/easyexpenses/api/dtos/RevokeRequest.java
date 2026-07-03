package com.easyexpenses.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record RevokeRequest(
        @NotBlank String refreshToken
) {
}
