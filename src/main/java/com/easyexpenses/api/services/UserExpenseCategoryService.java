package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.errors.ErrorCode;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserExpenseCategoryService {
    private final UserExpenseCategoryRepository userExpenseCategoryRepository;

    public UserExpenseCategoryService(UserExpenseCategoryRepository userExpenseCategoryRepository) {
        this.userExpenseCategoryRepository = userExpenseCategoryRepository;
    }

    public Set<UserExpenseCategory> getAllWithUserSubExpenseCategoriesByUserId(Long userId) {
        return userExpenseCategoryRepository.joinFetchCategoriesAndSubcategoriesAndFilterByUserId(userId);
    }

    public UserExpenseCategory getByIdAndUserId(Long id, Long userId) {
        return userExpenseCategoryRepository.findByIdAndUserProfileUserId(id, userId).orElseThrow(() ->
                new ResourceNotFoundException("User Expense Category was not found for this user!", ErrorCode.USER_EXPENSE_CATEGORY_NOT_FOUND));
    }
}
