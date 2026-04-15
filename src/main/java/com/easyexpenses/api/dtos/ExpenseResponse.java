package com.easyexpenses.api.dtos;

import java.util.Date;

public record ExpenseResponse(Long id, Long userId, Long categoryId, Long subCategoryId, Long paymentTypeId, Date date, Double value, String comment) {
}
