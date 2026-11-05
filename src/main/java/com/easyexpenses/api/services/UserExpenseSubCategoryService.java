package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseSubCategory;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserExpenseSubCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class UserExpenseSubCategoryService {
    private final UserExpenseSubCategoryRepository userExpenseSubCategoryRepository;

    public UserExpenseSubCategoryService(UserExpenseSubCategoryRepository userExpenseSubCategoryRepository) {
        this.userExpenseSubCategoryRepository = userExpenseSubCategoryRepository;
    }

    public UserExpenseSubCategory getByIdAndCategoryId(Long id, Long categoryId) {
        return userExpenseSubCategoryRepository.findByIdAndUserExpenseCategoryId(id, categoryId).orElseThrow(() ->
                new ResourceNotFoundException("User Expense Sub Category was not found for this User Expense Category!", ErrorCode.USER_EXPENSE_SUB_CATEGORY_NOT_FOUND));
    }
}
