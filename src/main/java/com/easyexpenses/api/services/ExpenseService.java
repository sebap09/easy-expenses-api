package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.ExpenseBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    public List<ExpenseResponse> getAllExpenses(UserProfile userProfile) {
        return expenseRepository.findAllByUserId(userProfile.getUserId())
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    public ExpenseResponse addNewExpense(UserProfile userProfile, AddNewExpenseRequest addNewExpenseRequest) {
        UserPaymentMethod userPaymentMethod = userProfile.getUserPaymentMethods()
                .stream()
                .filter(paymentMethod -> Objects.equals(paymentMethod.getId(), addNewExpenseRequest.userPaymentMethodId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User payment method not found!"));
        UserExpenseCategory userExpenseCategory = userProfile.getUserExpenseCategories()
                .stream()
                .filter(category -> Objects.equals(category.getId(), addNewExpenseRequest.categoryId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User category not found!"));
        UserExpenseSubCategory userExpenseSubCategory = userExpenseCategory.getSubCategoriesRelatedWithThisCategory()
                .stream()
                .filter(subCategory -> Objects.equals(subCategory.getId(), addNewExpenseRequest.subCategoryId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User sub category not found!"));

        Expense expense = new ExpenseBuilder()
                .userProfile(userProfile)
                .userPaymentMethod(userPaymentMethod)
                .userExpenseCategory(userExpenseCategory)
                .userExpenseSubCategory(userExpenseSubCategory)
                .value(addNewExpenseRequest.value())
                .date(addNewExpenseRequest.date())
                .comment(addNewExpenseRequest.comment())
                .build();

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
}
