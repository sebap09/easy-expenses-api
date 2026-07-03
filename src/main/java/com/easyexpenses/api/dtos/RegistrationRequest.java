package com.easyexpenses.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank String rawPassword
) {
}
