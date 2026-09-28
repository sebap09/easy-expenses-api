package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.UserExpenseCategory;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserExpenseCategoryService {
    private final UserExpenseCategoryRepository userExpenseCategoryRepository;

    @Autowired
    public UserExpenseCategoryService(UserExpenseCategoryRepository userExpenseCategoryRepository) {
        this.userExpenseCategoryRepository = userExpenseCategoryRepository;
    }

    public UserExpenseCategory getUserExpenseCategory(Long id){
        return userExpenseCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User Expense Category not found: " + id));
    }

    public List<UserExpenseCategory> getAllUserExpenseCategories(Long userId) {
        return userExpenseCategoryRepository.findAllByUserId(userId);
    }

    public List<UserExpenseCategory> saveAll(List<UserExpenseCategory> categories) {
        return userExpenseCategoryRepository.saveAll(categories);
    }
}
