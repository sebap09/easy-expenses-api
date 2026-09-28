package com.easyexpenses.api.dtos.userprofile.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserProfileRequest(@NotEmpty List<String> paymentMethods, @Valid @NotEmpty List<CategoryReq> categories) {
}
