package com.easyexpenses.api.dtos.userprofile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CategoryReq(@NotBlank String name, @NotEmpty List<String> subCategories) {
}
