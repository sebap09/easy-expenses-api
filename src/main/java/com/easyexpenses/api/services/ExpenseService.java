package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.ExpenseBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.dtos.PageResponse;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.mappers.PageMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final PageMapper pageMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, PageMapper pageMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.pageMapper = pageMapper;
    }

    public List<ExpenseResponse> getAllExpenses(UserProfile userProfile) {
        return expenseRepository.findAllByUserId(userProfile.getUserId())
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    public PageResponse<ExpenseResponse> getAllExpensesPaginated(UserProfile userProfile, Pageable pageable) {
        Page<ExpenseResponse> allExpensesPaginated = expenseRepository
                .findAllByUserIdWithPagination(userProfile.getUserId(), pageable)
                .map(expenseMapper::toResponse);

        return pageMapper.from(allExpensesPaginated);
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
