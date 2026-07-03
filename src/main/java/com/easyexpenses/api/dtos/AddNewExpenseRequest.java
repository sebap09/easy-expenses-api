package com.easyexpenses.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record AddNewExpenseRequest (
        @NotNull Long userId,
        @NotNull Long categoryId,
        @NotNull Long subCategoryId,
        @NotNull Long userPaymentMethodId,
        @NotNull Date date,
        @NotNull Double value,
        @NotBlank String comment
){}
