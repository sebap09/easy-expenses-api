package com.easyexpenses.api.mappers;

import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.Expense;
import org.springframework.stereotype.Service;

@Service
public class ExpenseMapper {
    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getUser().getId(),
                expense.getCategoryId(),
                expense.getSubcategoryId(),
                expense.getPaymentTypeId(),
                expense.getDate(),
                expense.getValue(),
                expense.getComment()
        );
    }
}
