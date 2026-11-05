package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.ExpenseBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.dtos.PageResponse;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.mappers.PageMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final PageMapper pageMapper;
    private final UserProfileService userProfileService;
    private final UserExpenseCategoryService userExpenseCategoryService;
    private final UserPaymentMethodService userPaymentMethodService;
    private final UserExpenseSubCategoryService userExpenseSubCategoryService;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper, PageMapper pageMapper, UserProfileService userProfileService, UserExpenseCategoryService userExpenseCategoryService, UserPaymentMethodService userPaymentMethodService, UserExpenseSubCategoryService userExpenseSubCategoryService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.pageMapper = pageMapper;
        this.userProfileService = userProfileService;
        this.userExpenseCategoryService = userExpenseCategoryService;
        this.userPaymentMethodService = userPaymentMethodService;
        this.userExpenseSubCategoryService = userExpenseSubCategoryService;
    }
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses(Long userId) {
        return expenseRepository.findByUserProfileUserId(userId)
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> getAllExpensesPaginated(Long userId, Pageable pageable) {
        Page<ExpenseResponse> allExpensesPaginated = expenseRepository
                .findByUserProfileUserId(userId, pageable)
                .map(expenseMapper::toResponse);

        return pageMapper.from(allExpensesPaginated);
    }

    @Transactional
    public ExpenseResponse addNewExpense(Long userId, AddNewExpenseRequest addNewExpenseRequest) {
        UserProfile userProfile = userProfileService.getUserProfileById(userId);

        UserPaymentMethod selectedUserPaymentMethod = userPaymentMethodService.getByIdAndUserId(addNewExpenseRequest.userPaymentMethodId(), userId);
        UserExpenseCategory selectedUserExpenseCategory = userExpenseCategoryService.getByIdAndUserId(addNewExpenseRequest.categoryId(), userId);
        UserExpenseSubCategory selectedUserExpenseSubCategory = userExpenseSubCategoryService.getByIdAndCategoryId(addNewExpenseRequest.subCategoryId(), addNewExpenseRequest.categoryId());

        Expense expense = new ExpenseBuilder()
                .value(addNewExpenseRequest.value())
                .date(addNewExpenseRequest.date())
                .comment(addNewExpenseRequest.comment())
                .build();

        expense.setUserPaymentMethod(selectedUserPaymentMethod);
        expense.setUserExpenseCategory(selectedUserExpenseCategory);
        expense.setUserExpenseSubCategory(selectedUserExpenseSubCategory);
        userProfile.addNewExpense(expense);

        return expenseMapper.toResponse(expenseRepository.save(expense));
    }
}
