package com.easyexpenses.api.dtos;

import java.util.Date;

public record ExpenseResponse(Long id, Long categoryId, Long subCategoryId, Long userPaymentMethodId, Date date, Double value, String comment) {
}
