package com.easyexpenses.api.dtos;

import java.util.Date;

public record AddNewExpenseRequest (Long userId, Long categoryId, Long subCategoryId, Long paymentTypeId, Date date, Double value, String comment){}
