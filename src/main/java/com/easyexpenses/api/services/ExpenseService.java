package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.ExpenseBuilder;
import com.easyexpenses.api.dtos.AddNewExpenseRequest;
import com.easyexpenses.api.dtos.ExpenseResponse;
import com.easyexpenses.api.entities.*;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ValidationException;
import com.easyexpenses.api.mappers.ExpenseMapper;
import com.easyexpenses.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserProfileService userProfileService;
    private final UserPaymentMethodService userPaymentMethodService;
    private final UserExpenseCategoryService userExpenseCategoryService;
    private final UserExpenseSubCategoryService userExpenseSubCategoryService;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, UserProfileService userProfileService, UserPaymentMethodService userPaymentMethodService, UserExpenseCategoryService userExpenseCategoryService, UserExpenseSubCategoryService userExpenseSubCategoryService, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.userProfileService = userProfileService;
        this.userPaymentMethodService = userPaymentMethodService;
        this.userExpenseCategoryService = userExpenseCategoryService;
        this.userExpenseSubCategoryService = userExpenseSubCategoryService;
        this.expenseMapper = expenseMapper;
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll()
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    public ExpenseResponse addNewExpense(AddNewExpenseRequest addNewExpenseRequest) {
        UserProfile userProfile = userProfileService.getUserProfile(addNewExpenseRequest.userId());
        UserPaymentMethod userPaymentMethod = userPaymentMethodService.getUserPaymentMethod(addNewExpenseRequest.userPaymentMethodId());
        UserExpenseCategory userExpenseCategory = userExpenseCategoryService.getUserExpenseCategory(addNewExpenseRequest.categoryId());
        UserExpenseSubCategory userExpenseSubCategory = userExpenseSubCategoryService.getUserExpenseSubCategory(addNewExpenseRequest.subCategoryId());

        validateDomainConstraints(userProfile, userPaymentMethod, userExpenseCategory, userExpenseSubCategory);

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

    private boolean isUserIdConsistentAcrossDomain(
            UserProfile userProfile,
            UserPaymentMethod userPaymentMethod,
            UserExpenseCategory userExpenseCategory,
            UserExpenseSubCategory userExpenseSubCategory
    ) {
        Long userId = userProfile.getUserId();
        return Objects.equals(userId, userPaymentMethod.getUserProfile().getUserId()) &&
                Objects.equals(userId, userExpenseCategory.getUserProfile().getUserId()) &&
                Objects.equals(userId, userExpenseSubCategory.getUserProfile().getUserId());
    }

    private boolean isSubCategoryAssignedToProperCategory(
            UserExpenseCategory userExpenseCategory,
            UserExpenseSubCategory userExpenseSubCategory
    ) {
        return Objects.equals(userExpenseSubCategory.getUserExpenseCategory().getId(), userExpenseCategory.getId());
    }

    private void validateDomainConstraints(
            UserProfile userProfile,
            UserPaymentMethod userPaymentMethod,
            UserExpenseCategory userExpenseCategory,
            UserExpenseSubCategory userExpenseSubCategory
    ){
        if(!isUserIdConsistentAcrossDomain(
                userProfile,
                userPaymentMethod,
                userExpenseCategory,
                userExpenseSubCategory))
            throw new ValidationException("User Id does not match across domain data", ErrorCode.INVALID_USER_RELATIONSHIP);

        if(!isSubCategoryAssignedToProperCategory(userExpenseCategory, userExpenseSubCategory))
            throw new ValidationException("Sub-category assignment not correct", ErrorCode.SUBCATEGORY_CATEGORY_MISMATCH);
    }
}
